package sn.edu.ept.postgram.feedservice.dto.event;

import java.util.UUID;

public record PostInteractionEvent(
        UUID postId,
        long likesCount,
        long commentsCount
) {
}
