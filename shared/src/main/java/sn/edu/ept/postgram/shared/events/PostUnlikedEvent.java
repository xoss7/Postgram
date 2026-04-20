package sn.edu.ept.postgram.shared.events;

import java.util.UUID;

public record PostUnlikedEvent(UUID postId, UUID userId) {}