package sn.edu.ept.postgram.shared.events;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PostDeletedEvent(
        UUID postId,
        UUID authorId
) {}
