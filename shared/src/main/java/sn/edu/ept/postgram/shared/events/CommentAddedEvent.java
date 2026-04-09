package sn.edu.ept.postgram.shared.events;

import java.util.UUID;

public record CommentAddedEvent(
        UUID postAuthorId,
        UUID commenterId,
        String commenterUsername,
        UUID postId
) {}
