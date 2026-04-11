package sn.edu.ept.postgram.messaging.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import sn.edu.ept.postgram.messaging.dto.ConversationResponseDto;
import sn.edu.ept.postgram.messaging.dto.MessageResponseDto;
import sn.edu.ept.postgram.messaging.services.MessageService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponseDto>> getConversations(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getClaimAsString("user_id"));
        return ResponseEntity.ok(messageService.getConversations(userId));
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<Page<MessageResponseDto>> getMessages(
            @PathVariable UUID conversationId,
            @AuthenticationPrincipal Jwt jwt,
            Pageable pageable) {

        UUID userId = UUID.fromString(jwt.getClaimAsString("user_id"));
        return ResponseEntity.ok(
                messageService.getMessages(conversationId, userId, pageable)
        );
    }

    @PatchMapping("/conversation/{conversationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID conversationId, @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getClaimAsString("user_id"));
        messageService.markAsRead(conversationId, userId);
        return ResponseEntity.noContent().build();
    }
}
