package sn.edu.ept.postgram.shared.events;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CommentAddedEvent(
        UUID commentId,
        UUID postAuthorId,
        UUID commenterId,
        String commenterUsername,
        UUID postId
) {}
