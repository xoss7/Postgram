package sn.edu.ept.postgram.contentservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.edu.ept.postgram.contentservice.dto.CommentRequestDto;
import sn.edu.ept.postgram.contentservice.dto.CommentResponseDto;
import sn.edu.ept.postgram.contentservice.service.CommentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/content")
public class InteractionController {

    private final CommentService commentService;

    public InteractionController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponseDto> addComment(
            @PathVariable UUID postId,
            @Valid @RequestBody CommentRequestDto requestDto) {
        return new ResponseEntity<>(commentService.addComment(postId, requestDto), HttpStatus.CREATED);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getCommentsForPost(@PathVariable UUID postId) {
        return ResponseEntity.ok(commentService.getCommentsForPost(postId));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
