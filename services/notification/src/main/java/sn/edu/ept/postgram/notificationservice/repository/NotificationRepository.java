package sn.edu.ept.postgram.notificationservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sn.edu.ept.postgram.notificationservice.entity.Notification;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId, Pageable pageable);

    @Modifying
    @Query("UPDATE Notification SET readAt = :now WHERE recipientId = :recipientId AND readAt IS NULL")
    void markAllAsRead(UUID recipientId, LocalDateTime now);

    long countByRecipientIdAndReadAtIsNull(UUID recipientId);
}