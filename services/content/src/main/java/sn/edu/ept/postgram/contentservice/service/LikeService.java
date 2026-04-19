package sn.edu.ept.postgram.contentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sn.edu.ept.postgram.contentservice.config.EventPublisher;
import sn.edu.ept.postgram.contentservice.entity.Like;
import sn.edu.ept.postgram.contentservice.entity.Post;
import sn.edu.ept.postgram.contentservice.repository.LikeRepository;
import sn.edu.ept.postgram.contentservice.repository.PostRepository;
import sn.edu.ept.postgram.shared.events.KafkaTopics;
import sn.edu.ept.postgram.shared.events.PostLikedEvent;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public void likePost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        UUID userId = CurrentUserClaims.userId();

        if (likeRepository.existsByPostIdAndAuthorId(postId, userId)) {
            return; // Already liked
        }

        Like like = Like.builder()
                .post(post)
                .authorId(userId)
                .authorUsername(CurrentUserClaims.username())
                .build();

        likeRepository.save(like);

        PostLikedEvent event = new PostLikedEvent(
                post.getAuthorId(),
                userId,
                CurrentUserClaims.username(),
                postId
        );

        eventPublisher.publish(KafkaTopics.POST_LIKED, postId.toString(), event);
    }

    @Transactional
    public void unlikePost(UUID postId) {
        UUID userId = CurrentUserClaims.userId();
        likeRepository.findByPostIdAndAuthorId(postId, userId)
                .ifPresent(likeRepository::delete);
    }
}
