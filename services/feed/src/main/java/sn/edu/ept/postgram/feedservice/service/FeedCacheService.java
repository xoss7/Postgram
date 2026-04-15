package sn.edu.ept.postgram.feedservice.service;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import sn.edu.ept.postgram.feedservice.config.FeedProperties;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FeedCacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final FeedProperties properties;

    public FeedCacheService(RedisTemplate<String, String> redisTemplate, FeedProperties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    public void addPostToUserFeed(UUID userId, UUID postId, double score) {
        String userFeedKey = userFeedKey(userId);
        String postIdValue = postId.toString();

        redisTemplate.opsForZSet().add(userFeedKey, postIdValue, score);
        redisTemplate.opsForSet().add(postOwnersKey(postId), userId.toString());
        touch(userFeedKey);
        touch(postOwnersKey(postId));
    }

    public void addPostToMultipleFeeds(Set<UUID> userIds, UUID postId, double score) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        String postIdValue = postId.toString();
        String ownersKey = postOwnersKey(postId);

        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (UUID userId : userIds) {
                String userFeedKey = userFeedKey(userId);
                String userIdValue = userId.toString();

                // ZSet feed:user:id -> postId (score = timestamp)
                connection.zSetCommands().zAdd(
                        userFeedKey.getBytes(),
                        score,
                        postIdValue.getBytes()
                );

                // Set feed:post:id:owners -> userId (pour suppression facile)
                connection.setCommands().sAdd(
                        ownersKey.getBytes(),
                        userIdValue.getBytes()
                );

                // Expire pour nettoyage auto
                connection.keyCommands().expire(
                        userFeedKey.getBytes(),
                        Duration.ofHours(properties.getCache().getTtlHours()).getSeconds()
                );
            }

            connection.keyCommands().expire(
                    ownersKey.getBytes(),
                    Duration.ofHours(properties.getCache().getTtlHours()).getSeconds()
            );

            return null;
        });
    }

    public Set<UUID> getFeedPostIds(UUID userId, int offset, int limit) {
        if (limit <= 0) {
            return Collections.emptySet();
        }

        Set<String> raw = redisTemplate.opsForZSet()
                .reverseRange(userFeedKey(userId), offset, offset + limit - 1);

        if (raw == null || raw.isEmpty()) {
            return Collections.emptySet();
        }

        return raw.stream()
                .map(UUID::fromString)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public long size(UUID userId) {
        Long size = redisTemplate.opsForZSet().zCard(userFeedKey(userId));
        return size == null ? 0L : size;
    }

    public boolean hasFeed(UUID userId) {
        Boolean exists = redisTemplate.hasKey(userFeedKey(userId));
        return Boolean.TRUE.equals(exists);
    }

    public void incrementScore(UUID userId, UUID postId, double delta) {
        redisTemplate.opsForZSet().incrementScore(userFeedKey(userId), postId.toString(), delta);
        touch(userFeedKey(userId));
    }

    public void removePostsFromUserFeed(UUID userId, Set<UUID> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return;
        }

        String userFeedKey = userFeedKey(userId);
        String[] postIdValues = postIds.stream().map(UUID::toString).toArray(String[]::new);

        redisTemplate.opsForZSet().remove(userFeedKey, (Object[]) postIdValues);

        for (UUID postId : postIds) {
            redisTemplate.opsForSet().remove(postOwnersKey(postId), userId.toString());
        }
    }

    public void removePostFromUserFeed(UUID userId, UUID postId) {
        redisTemplate.opsForZSet().remove(userFeedKey(userId), postId.toString());
        redisTemplate.opsForSet().remove(postOwnersKey(postId), userId.toString());
    }

    public void removePostEverywhere(UUID postId) {
        String ownersKey = postOwnersKey(postId);
        Set<String> ownerIds = redisTemplate.opsForSet().members(ownersKey);
        if (ownerIds != null) {
            for (String ownerId : ownerIds) {
                redisTemplate.opsForZSet().remove(userFeedKey(UUID.fromString(ownerId)), postId.toString());
            }
        }
        redisTemplate.delete(ownersKey);
    }

    public Set<UUID> getFeedOwners(UUID postId) {
        Set<String> raw = redisTemplate.opsForSet().members(postOwnersKey(postId));
        if (raw == null || raw.isEmpty()) {
            return Collections.emptySet();
        }
        return raw.stream()
                .map(UUID::fromString)
                .collect(Collectors.toSet());
    }

    public Double getScore(UUID userId, UUID postId) {
        return redisTemplate.opsForZSet().score(userFeedKey(userId), postId.toString());
    }

    public void updateScore(UUID userId, UUID postId, double score) {
        redisTemplate.opsForZSet().add(userFeedKey(userId), postId.toString(), score);
        touch(userFeedKey(userId));
    }

    public void clearFeed(UUID userId) {
        Set<String> postIds = redisTemplate.opsForZSet().range(userFeedKey(userId), 0, -1);
        if (postIds != null) {
            for (String postId : postIds) {
                redisTemplate.opsForSet().remove(postOwnersKey(UUID.fromString(postId)), userId.toString());
            }
        }
        redisTemplate.delete(userFeedKey(userId));
    }

    public Set<ZSetOperations.TypedTuple<String>> reverseRangeWithScores(UUID userId, int offset, int limit) {
        if (limit <= 0) {
            return Collections.emptySet();
        }
        Set<ZSetOperations.TypedTuple<String>> values = redisTemplate.opsForZSet()
                .reverseRangeWithScores(userFeedKey(userId), offset, offset + limit - 1);
        return values == null ? Collections.emptySet() : values;
    }

    private void touch(String key) {
        redisTemplate.expire(key, Duration.ofHours(properties.getCache().getTtlHours()));
    }

    private String userFeedKey(UUID userId) {
        return properties.getCache().getPrefix() + userId;
    }

    private String postOwnersKey(UUID postId) {
        return properties.getCache().getPostOwnersPrefix() + postId + ":owners";
    }
}
