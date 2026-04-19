package sn.edu.ept.postgram.shared.events;

import java.util.UUID;

public record MessageSentEvent(
        UUID messageId,
        UUID conversationId,
        UUID senderId,
        String senderUsername,
        UUID receiverId
) {
}
