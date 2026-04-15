package sn.edu.ept.postgram.feedservice.dto.event;

import java.util.UUID;

public record PostDeletedEvent(UUID postId) {
}
