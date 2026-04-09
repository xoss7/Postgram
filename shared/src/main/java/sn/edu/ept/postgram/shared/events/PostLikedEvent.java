package sn.edu.ept.postgram.shared.events;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PostLikedEvent(
        UUID postAuthorId,
        UUID likerId,
        String likerUsername,
        UUID postId
) {}