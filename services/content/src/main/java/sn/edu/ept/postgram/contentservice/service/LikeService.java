package sn.edu.ept.postgram.contentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.edu.ept.postgram.contentservice.config.KafkaConfig;
import sn.edu.ept.postgram.contentservice.entity.Like;
import sn.edu.ept.postgram.contentservice.entity.Post;
import sn.edu.ept.postgram.contentservice.repository.LikeRepository;
import sn.edu.ept.postgram.contentservice.repository.PostRepository;
import sn.edu.ept.postgram.shared.events.KafkaTopics;
import sn.edu.ept.postgram.shared.events.PostLikedEvent;

import java.util.UUID;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final KafkaConfig.EventPublisher eventPublisher;

    public LikeService(LikeRepository likeRepository, PostRepository postRepository, KafkaConfig.EventPublisher eventPublisher) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void likePost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        UUID userId = getCurrentUserId();
        String username = getCurrentUsername();

        if (likeRepository.existsByPostAndUserId(post, userId)) {
            return;
        }

        Like like = Like.builder()
                .post(post)
                .userId(userId)
                .username(username)
                .build();

        likeRepository.save(like);

        PostLikedEvent event = new PostLikedEvent(
                post.getAuthorId(),
                userId,
                username,
                post.getId()
        );

        eventPublisher.publish(KafkaTopics.POST_LIKED, post.getId().toString(), event);
    }

    @Transactional
    public void unlikePost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        UUID userId = getCurrentUserId();
        likeRepository.deleteByPostAndUserId(post, userId);
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
