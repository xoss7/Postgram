package sn.edu.ept.postgram.shared.events;

import java.util.UUID;

public record PostLikedEvent(
        UUID postAuthorId,
        UUID likerId,
        String likerUsername,
        UUID postId
) {}