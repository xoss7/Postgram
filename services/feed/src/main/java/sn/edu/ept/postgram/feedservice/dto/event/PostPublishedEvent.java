package sn.edu.ept.postgram.feedservice.dto.event;

import java.time.Instant;
import java.util.UUID;

public record PostPublishedEvent(
        UUID postId,
        UUID authorId,
        String caption,
        String mediaUrl,
        String visibility,
        Instant createdAt,
        long likesCount,
        long commentsCount
) {
}
