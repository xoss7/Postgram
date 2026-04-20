package sn.edu.ept.postgram.contentservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.edu.ept.postgram.contentservice.dto.CommentResponse;
import sn.edu.ept.postgram.contentservice.dto.CreateCommentRequest;
import sn.edu.ept.postgram.contentservice.service.CommentService;
import sn.edu.ept.postgram.contentservice.utils.CurrentUserClaims;

import java.util.UUID;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable UUID postId,
            @RequestBody @Valid CreateCommentRequest request) {
        UUID userId = CurrentUserClaims.userId();
        String username = CurrentUserClaims.username();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(userId, username, postId, request));
    }

    @GetMapping
    public ResponseEntity<Page<CommentResponse>> getComments(
            @PathVariable UUID postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                commentService.getComments(postId, PageRequest.of(page, size))
        );
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable UUID commentId) {
        UUID userId = CurrentUserClaims.userId();
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}