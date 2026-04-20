package sn.edu.ept.postgram.contentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.edu.ept.postgram.contentservice.config.EventPublisher;
import sn.edu.ept.postgram.contentservice.entity.Like;
import sn.edu.ept.postgram.contentservice.entity.Post;
import sn.edu.ept.postgram.contentservice.repository.LikeRepository;
import sn.edu.ept.postgram.contentservice.repository.PostRepository;
import sn.edu.ept.postgram.shared.events.KafkaTopics;
import sn.edu.ept.postgram.shared.events.PostLikedEvent;
import sn.edu.ept.postgram.shared.events.PostUnlikedEvent;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final EventPublisher eventPublisher;

    public void like(UUID userId, String username, UUID postId) {
        if (likeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new RuntimeException("Already liked");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Like like = Like.builder()
                .postId(postId)
                .userId(userId)
                .username(username)
                .build();

        likeRepository.save(like);

        post.setLikesCount(post.getLikesCount() + 1);
        postRepository.save(post);

        eventPublisher.publish(
                KafkaTopics.POST_LIKED,
                postId.toString(),
                new PostLikedEvent(post.getAuthorId(), userId, username, postId)
        );
    }

    public void unlike(UUID userId, UUID postId) {
        if (!likeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new RuntimeException("Not liked");
        }

        likeRepository.deleteByPostIdAndUserId(postId, userId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
        postRepository.save(post);

        eventPublisher.publish(
                KafkaTopics.POST_UNLIKED,
                postId.toString(),
                new PostUnlikedEvent(postId, userId)
        );
    }

    public boolean isLiked(UUID userId, UUID postId) {
        return likeRepository.existsByPostIdAndUserId(postId, userId);
    }
}