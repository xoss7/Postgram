package sn.edu.ept.postgram.messaging.dto;

import sn.edu.ept.postgram.messaging.entity.Conversation;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConversationResponseDto(
        UUID id,
        UUID participantA,
        UUID participantB,
        LocalDateTime lastMessageAt,
        LocalDateTime createdAt
) {
    public static ConversationResponseDto from(Conversation c) {
        return new ConversationResponseDto(
                c.getId(),
                c.getParticipantA(),
                c.getParticipantB(),
                c.getLastMessageAt(),
                c.getCreatedAt());
    }
}
