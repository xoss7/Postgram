package sn.edu.ept.postgram.messaging.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.edu.ept.postgram.messaging.dto.ConversationResponseDto;
import sn.edu.ept.postgram.messaging.dto.MessageResponseDto;
import sn.edu.ept.postgram.messaging.dto.SendMessageRequest;
import sn.edu.ept.postgram.messaging.entity.Conversation;
import sn.edu.ept.postgram.messaging.entity.Message;
import sn.edu.ept.postgram.messaging.repository.ConversationRepository;
import sn.edu.ept.postgram.messaging.repository.MessageRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void sendMessage(UUID senderId, String senderUsername, SendMessageRequest request) {
        Conversation conversation = findOrCreateConversation(senderId, request.receiverId());

        Message message = Message.builder()
                .conversationId(conversation.getId())
                .senderId(senderId)
                .senderUsername(senderUsername)
                .content(request.content())
                .build();
        messageRepository.save(message);

        conversation.setLastMessageAt(message.getCreatedAt());
        conversationRepository.save(conversation);

        MessageResponseDto response = MessageResponseDto.from(message);

        messagingTemplate.convertAndSendToUser(
                request.receiverId().toString(),
                "/queue/messages",
                response
        );
    }

    @Transactional(readOnly = true)
    public Page<MessageResponseDto> getMessages(UUID conversationId, UUID userId, Pageable pageable) {
        Conversation conversation = conversationRepository.findById(conversationId).orElseThrow(
                () -> new EntityNotFoundException("Conversation not found"));

        if (!conversation.getParticipantA().equals(userId) && !conversation.getParticipantB().equals(userId)) {
            throw new RuntimeException("Forbidden");
        }

        return messageRepository
                .findAllByConversationIdOrderByCreatedAtDesc(conversationId, pageable)
                .map(MessageResponseDto::from);
    }

    @Transactional(readOnly = true)
    public List<ConversationResponseDto> getConversations(UUID userId) {
        return conversationRepository.findAllByUserId(userId)
                .stream()
                .map(ConversationResponseDto::from)
                .toList();
    }

    public void markAsRead(UUID conversationId, UUID userId) {
        messageRepository.markAsRead(conversationId, userId, LocalDateTime.now());
    }

    private Conversation findOrCreateConversation(UUID userA, UUID userB) {
        UUID participantA = userA.compareTo(userB) < 0 ? userA : userB;
        UUID participantB = userA.compareTo(userB) < 0 ? userB : userA;

        return conversationRepository.findByParticipantAAndParticipantB(userA, userB).orElseGet(
                () -> conversationRepository.save(
                        Conversation.builder()
                                .participantA(participantA)
                                .participantB(participantB)
                                .build()
                ));

    }
}