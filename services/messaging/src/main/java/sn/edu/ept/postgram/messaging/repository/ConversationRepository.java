package sn.edu.ept.postgram.messaging.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sn.edu.ept.postgram.messaging.entity.Conversation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    Optional<Conversation> findByParticipantAAndParticipantB(UUID participantA, UUID participantB);

    @Query("""
        SELECT c FROM Conversation c
        WHERE c.participantA = :userId OR c.participantB = :userId
        ORDER BY c.lastMessageAt DESC
        """)
    List<Conversation> findAllByUserId(UUID userId);
}
