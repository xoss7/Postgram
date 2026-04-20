package sn.edu.ept.postgram.contentservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import sn.edu.ept.postgram.contentservice.dto.CreatePostRequest;
import sn.edu.ept.postgram.contentservice.dto.PostResponse;
import sn.edu.ept.postgram.contentservice.dto.UpdatePostRequest;
import sn.edu.ept.postgram.contentservice.service.PostService;
import sn.edu.ept.postgram.contentservice.utils.CurrentUserClaims;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestBody @Valid CreatePostRequest request) {
        UUID userId = CurrentUserClaims.userId();
        String username = CurrentUserClaims.username();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.createPost(userId, username, request));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable UUID postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostResponse>> getUserPosts(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                postService.getUserPosts(userId, PageRequest.of(page, size))
        );
    }

    @GetMapping("/batch")
    public ResponseEntity<List<PostResponse>> getPostsByIds(
            @RequestParam List<UUID> ids) {
        return ResponseEntity.ok(postService.getPostsByIds(ids));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable UUID postId,
            @RequestBody UpdatePostRequest request) {
        UUID userId = CurrentUserClaims.userId();
        return ResponseEntity.ok(postService.updatePost(postId, userId, request));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable UUID postId) {
        UUID userId = CurrentUserClaims.userId();
        postService.deletePost(postId, userId);
        return ResponseEntity.noContent().build();
    }
}