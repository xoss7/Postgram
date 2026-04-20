package sn.edu.ept.postgram.feedservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import sn.edu.ept.postgram.feedservice.client.ContentServiceClient;
import sn.edu.ept.postgram.feedservice.config.RedisKeys;
import sn.edu.ept.postgram.feedservice.dto.FeedResponse;
import sn.edu.ept.postgram.feedservice.dto.PostResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ContentServiceClient contentServiceClient;

    @Value("${feed.default-page-size:20}")
    private int defaultPageSize;

    public FeedResponse getFeed(UUID userId, int page, int size) {
        String key = RedisKeys.feed(userId);
        long start = (long) page * (size == 0 ? defaultPageSize : size);
        long end = start + size - 1;

        // recuperer les postIds tries (plus récent/populaire en premier)
        Set<String> postIds = redisTemplate.opsForZSet()
                .reverseRange(key, start, end);

        if (postIds == null || postIds.isEmpty()) {
            return new FeedResponse(List.of(), page, size, 0);
        }

        List<UUID> ids = postIds.stream()
                .map(UUID::fromString)
                .toList();

        List<PostResponse> posts = contentServiceClient.getPostsByIds(ids);
        Long total = redisTemplate.opsForZSet().size(key);
        return new FeedResponse(posts, page, size, total != null ? total : 0);
    }
}