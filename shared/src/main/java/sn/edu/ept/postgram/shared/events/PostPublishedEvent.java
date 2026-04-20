package sn.edu.ept.postgram.shared.events;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record PostPublishedEvent(
        UUID postId,
        UUID authorId,
        String authorUsername,
        String visibility,
        LocalDateTime createdAt
) {}
