package sn.edu.ept.postgram.feedservice.dto.api;

import java.time.Instant;
import java.util.UUID;

public record FeedItemResponse(
        UUID postId,
        UUID authorId,
        String authorUsername,
        String authorProfileUrl,
        String caption,
        String mediaUrl,
        String visibility,
        long likesCount,
        long commentsCount,
        Instant createdAt
) {
}
