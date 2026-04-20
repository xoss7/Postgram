package sn.edu.ept.postgram.feedservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import sn.edu.ept.postgram.feedservice.client.ContentServiceClient;
import sn.edu.ept.postgram.feedservice.client.SocialServiceClient;
import sn.edu.ept.postgram.feedservice.config.RedisKeys;
import sn.edu.ept.postgram.feedservice.dto.PostResponse;

import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedFanoutService {

    private final RedisTemplate<String, String> redisTemplate;
    private final SocialServiceClient socialServiceClient;
    private final ContentServiceClient contentServiceClient;

    @Value("${feed.max-size:500}")
    private int maxFeedSize;

    @Value("${feed.backfill-limit:20}")
    private int backfillLimit;

    public void fanoutPost(UUID postId, UUID authorId, long score) {
        List<UUID> followers = socialServiceClient.getFollowers(authorId);
        log.info("Fanning out postId {} to {} followers", postId, followers.size());

        for (UUID followerId : followers) {
            String key = RedisKeys.feed(followerId);
            redisTemplate.opsForZSet().add(key, postId.toString(), score);
            redisTemplate.opsForZSet().removeRange(key, 0, -(maxFeedSize + 1));
        }
    }

    public void removePost(UUID postId, UUID authorId) {
        List<UUID> followers = socialServiceClient.getFollowers(authorId);
        for (UUID followerId : followers) {
            redisTemplate.opsForZSet().remove(RedisKeys.feed(followerId), postId.toString());
        }
    }

    // quand un user follow quelqu'un — charger ses posts recents dans le feed
    public void addFolloweePosts(UUID followerId, UUID followeeId) {
        log.info("Backfilling feed for {} after following {}", followerId, followeeId);

        List<PostResponse> recentPosts = contentServiceClient
                .getRecentPosts(followeeId, backfillLimit);

        if (recentPosts.isEmpty()) {
            log.info("No recent posts found for followee {}", followeeId);
            return;
        }

        String key = RedisKeys.feed(followerId);

        for (PostResponse post : recentPosts) {
            // ignorer les posts privés
            if ("PRIVATE".equals(post.visibility())) continue;

            long score = post.createdAt()
                    .toInstant(ZoneOffset.UTC)
                    .toEpochMilli();

            redisTemplate.opsForZSet().add(key, post.id().toString(), score);
        }

        // trim pour ne pas dépasser maxFeedSize
        redisTemplate.opsForZSet().removeRange(key, 0, -(maxFeedSize + 1));

        log.info("Added {} posts to feed of {} after following {}",
                recentPosts.size(), followerId, followeeId);
    }

    // quand un user unfollow — retirer ses posts du feed
    public void removeFolloweePosts(UUID followerId, UUID followeeId) {
        log.info("Cleaning feed for {} after unfollowing {}", followerId, followeeId);

        List<PostResponse> posts = contentServiceClient
                .getRecentPosts(followeeId, maxFeedSize);

        if (posts.isEmpty()) return;

        String key = RedisKeys.feed(followerId);

        List<String> postIds = posts.stream()
                .map(p -> p.id().toString())
                .toList();

        redisTemplate.opsForZSet().remove(key, postIds.toArray());

        log.info("Removed {} posts from feed of {} after unfollowing {}",
                postIds.size(), followerId, followeeId);
    }

    // augmenter le score d'un post liké dans tous les feeds
    public void boostPost(UUID postId, UUID authorId) {
        List<UUID> followers = socialServiceClient.getFollowers(authorId);
        for (UUID followerId : followers) {
            String key = RedisKeys.feed(followerId);
            redisTemplate.opsForZSet().incrementScore(key, postId.toString(), 1000);
        }
    }
}