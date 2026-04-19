package sn.edu.ept.postgram.contentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sn.edu.ept.postgram.contentservice.config.EventPublisher;
import sn.edu.ept.postgram.contentservice.dto.CommentRequestDto;
import sn.edu.ept.postgram.contentservice.dto.CommentResponseDto;
import sn.edu.ept.postgram.contentservice.entity.Comment;
import sn.edu.ept.postgram.contentservice.entity.Post;
import sn.edu.ept.postgram.contentservice.repository.CommentRepository;
import sn.edu.ept.postgram.contentservice.repository.PostRepository;
import sn.edu.ept.postgram.shared.events.CommentAddedEvent;
import sn.edu.ept.postgram.shared.events.KafkaTopics;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public CommentResponseDto addComment(UUID postId, CommentRequestDto requestDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        Comment comment = Comment.builder()
                .post(post)
                .authorId(CurrentUserClaims.userId())
                .authorUsername(CurrentUserClaims.username())
                .content(requestDto.content())
                .build();

        Comment savedComment = commentRepository.save(comment);

        CommentAddedEvent event = new CommentAddedEvent(
                post.getAuthorId(),
                savedComment.getAuthorId(),
                savedComment.getAuthorUsername(),
                postId
        );

        eventPublisher.publish(KafkaTopics.COMMENT_ADDED, postId.toString(), event);

        return mapToResponseDto(savedComment);
    }

    public List<CommentResponseDto> getCommentsByPost(UUID postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        if (!comment.getAuthorId().equals(CurrentUserClaims.userId()) && 
            !comment.getPost().getAuthorId().equals(CurrentUserClaims.userId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own comments or comments on your own posts");
        }

        commentRepository.delete(comment);
    }

    private CommentResponseDto mapToResponseDto(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getPost().getId(),
                comment.getAuthorId(),
                comment.getAuthorUsername(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
