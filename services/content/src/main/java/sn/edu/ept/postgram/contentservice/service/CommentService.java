package sn.edu.ept.postgram.contentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.edu.ept.postgram.contentservice.config.EventPublisher;
import sn.edu.ept.postgram.contentservice.dto.CommentResponse;
import sn.edu.ept.postgram.contentservice.dto.CreateCommentRequest;
import sn.edu.ept.postgram.contentservice.entity.Comment;
import sn.edu.ept.postgram.contentservice.entity.Post;
import sn.edu.ept.postgram.contentservice.repository.CommentRepository;
import sn.edu.ept.postgram.contentservice.repository.PostRepository;
import sn.edu.ept.postgram.shared.events.CommentAddedEvent;
import sn.edu.ept.postgram.shared.events.KafkaTopics;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final EventPublisher eventPublisher;

    public CommentResponse addComment(UUID authorId, String authorUsername,
                                      UUID postId, CreateCommentRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = Comment.builder()
                .postId(postId)
                .authorId(authorId)
                .authorUsername(authorUsername)
                .content(request.content())
                .build();

        commentRepository.save(comment);

        // incrémenter le compteur
        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

        eventPublisher.publish(
                KafkaTopics.COMMENT_ADDED,
                comment.getId().toString(),
                new CommentAddedEvent(
                        comment.getId(),
                        post.getAuthorId(),
                        authorId,
                        authorUsername,
                        postId
                )
        );

        return CommentResponse.from(comment);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> getComments(UUID postId, Pageable pageable) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId, pageable)
                .map(CommentResponse::from);
    }

    public void deleteComment(UUID commentId, UUID authorId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getAuthorId().equals(authorId)) {
            throw new RuntimeException("Forbidden");
        }

        commentRepository.delete(comment);

        Post post = postRepository.findById(comment.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setCommentsCount(Math.max(0, post.getCommentsCount() - 1));
        postRepository.save(post);
    }
}