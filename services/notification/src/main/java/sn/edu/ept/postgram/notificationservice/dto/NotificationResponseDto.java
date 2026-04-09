package sn.edu.ept.postgram.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import sn.edu.ept.postgram.notificationservice.entity.Notification;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NotificationResponseDto(
        UUID id,
        UUID actorId,
        String actorUsername,
        UUID refId,
        LocalDateTime readAt,
        LocalDateTime createdAt
) {
    public static NotificationResponseDto from(Notification n) {
        return new NotificationResponseDto(
                n.getId(),
                n.getActorId(),
                n.getActorUsername(),
                n.getRefId(),
                n.getReadAt(),
                n.getCreatedAt()
        );
    }
}