package sn.edu.ept.postgram.contentservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.edu.ept.postgram.contentservice.service.LikeService;
import sn.edu.ept.postgram.contentservice.utils.CurrentUserClaims;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/posts/{postId}/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<Void> like(
            @PathVariable UUID postId) {
        UUID userId = CurrentUserClaims.userId();
        String username = CurrentUserClaims.username();
        likeService.like(userId, username, postId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> unlike(
            @PathVariable UUID postId) {
        UUID userId = CurrentUserClaims.userId();
        likeService.unlike(userId, postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/is-liked")
    public ResponseEntity<Map<String, Boolean>> isLiked(
            @PathVariable UUID postId) {
        UUID userId = CurrentUserClaims.userId();
        return ResponseEntity.ok(Map.of("liked", likeService.isLiked(userId, postId)));
    }
}