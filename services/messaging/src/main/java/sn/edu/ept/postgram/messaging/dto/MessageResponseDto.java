package sn.edu.ept.postgram.messaging.dto;

import sn.edu.ept.postgram.messaging.entity.Message;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageResponseDto(
        UUID id,
        UUID conversationId,
        UUID senderId,
        String senderUsername,
        String content,
        boolean read,
        LocalDateTime createdAt
) {
    public static MessageResponseDto from(Message m) {
        return new MessageResponseDto(
                m.getId(),
                m.getConversationId(),
                m.getSenderId(),
                m.getSenderUsername(),
                m.getContent(),
                m.isRead(),
                m.getCreatedAt()
        );
    }
}
