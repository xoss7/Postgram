package sn.edu.ept.postgram.notificationservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.edu.ept.postgram.notificationservice.dto.NotificationResponseDto;
import sn.edu.ept.postgram.notificationservice.entity.Notification;
import sn.edu.ept.postgram.notificationservice.model.NotificationType;
import sn.edu.ept.postgram.notificationservice.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    public void createNotification(UUID recipientId, UUID actorId, String actorUsername,
                                   NotificationType type, UUID refId) {

        // No notification if recipient is the actor (e.g. liking your own post)
        if (recipientId.equals(actorId)) return;

        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .actorId(actorId)
                .actorUsername(actorUsername)
                .type(type)
                .refId(refId)
                .build();
        notificationRepository.save(notification);

        simpMessagingTemplate.convertAndSendToUser(
                refId.toString(),
                "/queue/notification",
                NotificationResponseDto.from(notification)
        );
    }

    @Transactional(readOnly = true)
    public Page<Notification> getNotifications(UUID recipientId, Pageable pageable) {
            return notificationRepository
                    .findByRecipientIdOrderByCreatedAtDesc(recipientId, pageable);
    }

    @Transactional(readOnly = true)
    public long countUnRead(UUID recipientId) {
        return notificationRepository
                .countByRecipientIdAndReadAtIsNull(recipientId);
    }

    @Transactional
    public void markAllAsRead(UUID recipientId) {
        notificationRepository.markAllAsRead(recipientId, LocalDateTime.now());
    }

    @Transactional
    public void markAsRead(UUID recipientId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification with id " + notificationId + " not found"));

        // return 404 if notification does not belong to the user making the request
        if (!notification.getRecipientId().equals(recipientId)) {
            throw new EntityNotFoundException("Notification with id " + notificationId + " not found");
        }

        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }
}