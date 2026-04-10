package sn.edu.ept.postgram.notificationservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import sn.edu.ept.postgram.notificationservice.dto.NotificationResponseDto;
import sn.edu.ept.postgram.notificationservice.service.NotificationService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Page<NotificationResponseDto>> getNotifications(
            @AuthenticationPrincipal Jwt jwt, Pageable pageable) {

        UUID userId = UUID.fromString(jwt.getClaim("user_id"));
        Page<NotificationResponseDto> notifications = notificationService.getNotifications(userId, pageable)
                .map(NotificationResponseDto::from);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> countUnread(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getClaim("user_id"));
        long unreadCount = notificationService.countUnRead(userId);
        return ResponseEntity.ok(Map.of("unread", unreadCount));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getClaim("user_id"));
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        UUID userId = UUID.fromString(jwt.getClaim("user_id"));
        notificationService.markAsRead(userId, id);
        return ResponseEntity.noContent().build();
    }
}
