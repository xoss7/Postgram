package sn.edu.ept.postgram.shared.events;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PostPublishedEvent(
        UUID postId,
        UUID authorId,
        String authorUsername,
        String content,
        String mediaUrl,
        String visibility
) {}
