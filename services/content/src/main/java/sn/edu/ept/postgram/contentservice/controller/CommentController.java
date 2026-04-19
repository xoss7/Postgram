package sn.edu.ept.postgram.contentservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sn.edu.ept.postgram.contentservice.dto.CommentRequestDto;
import sn.edu.ept.postgram.contentservice.dto.CommentResponseDto;
import sn.edu.ept.postgram.contentservice.service.CommentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto addComment(@PathVariable UUID postId, @Valid @RequestBody CommentRequestDto requestDto) {
        return commentService.addComment(postId, requestDto);
    }

    @GetMapping
    public List<CommentResponseDto> getComments(@PathVariable UUID postId) {
        return commentService.getCommentsByPost(postId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable UUID commentId) {
        commentService.deleteComment(commentId);
    }
}
