package sn.edu.ept.postgram.contentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.edu.ept.postgram.contentservice.config.KafkaConfig;
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
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final KafkaConfig.EventPublisher eventPublisher;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, KafkaConfig.EventPublisher eventPublisher) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public CommentResponseDto addComment(UUID postId, CommentRequestDto requestDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        UUID authorId = getCurrentUserId();
        String authorUsername = getCurrentUsername();

        Comment comment = Comment.builder()
                .post(post)
                .authorId(authorId)
                .authorUsername(authorUsername)
                .content(requestDto.content())
                .build();

        Comment savedComment = commentRepository.save(comment);

        CommentAddedEvent event = new CommentAddedEvent(
                post.getAuthorId(),
                authorId,
                authorUsername,
                post.getId()
        );

        eventPublisher.publish(KafkaTopics.COMMENT_ADDED, post.getId().toString(), event);

        return mapToResponseDto(savedComment);
    }

    public List<CommentResponseDto> getCommentsForPost(UUID postId) {
        return commentRepository.findAllByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getAuthorId().equals(getCurrentUserId())) {
            throw new RuntimeException("Unauthorized to delete this comment");
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

    private UUID getCurrentUserId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userIdStr = jwt.getClaimAsString("user_id");
        if (userIdStr == null) {
            userIdStr = jwt.getSubject();
        }
        return UUID.fromString(userIdStr);
    }

    private String getCurrentUsername() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = jwt.getClaimAsString("preferred_username");
        if (username == null) {
            username = jwt.getSubject();
        }
        return username;
    }
}
