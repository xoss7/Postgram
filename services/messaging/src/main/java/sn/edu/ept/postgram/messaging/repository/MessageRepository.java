package sn.edu.ept.postgram.messaging.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sn.edu.ept.postgram.messaging.entity.Message;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    Page<Message> findAllByConversationIdOrderByCreatedAtDesc(UUID conversationId, Pageable pageable);

    @Modifying
    @Query("""
        UPDATE Message m SET m.readAt = :now WHERE m.conversationId = :conversationId
        AND m.senderId != :userId AND m.readAt IS NULL
        """)
    void markAsRead(UUID conversationId, UUID userId, LocalDateTime now);
}