package sn.edu.ept.postgram.feedservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import sn.edu.ept.postgram.feedservice.client.SocialServiceClient;
import sn.edu.ept.postgram.feedservice.config.RedisKeys;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedFanoutService {

    private final RedisTemplate<String, String> redisTemplate;
    private final SocialServiceClient socialServiceClient;

    @Value("${feed.max-size:500}")
    private int maxFeedSize;

    // ajouter un post dans le feed de tous les followers
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
        // non implémenté ici pour simplifier
        // en prod : récupérer les N derniers posts du followee et les ajouter au feed
        log.info("TODO: backfill feed for {} after following {}", followerId, followeeId);
    }

    // quand un user unfollow — retirer ses posts du feed
    public void removeFolloweePosts(UUID followerId, UUID followeeId) {
        // non implémenté ici pour simplifier
        log.info("TODO: cleanup feed for {} after unfollowing {}", followerId, followeeId);
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