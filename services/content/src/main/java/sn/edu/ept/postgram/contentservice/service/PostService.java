package sn.edu.ept.postgram.contentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.edu.ept.postgram.contentservice.config.KafkaConfig;
import sn.edu.ept.postgram.contentservice.dto.PostRequestDto;
import sn.edu.ept.postgram.contentservice.dto.PostResponseDto;
import sn.edu.ept.postgram.contentservice.entity.Post;
import sn.edu.ept.postgram.contentservice.repository.PostRepository;
import sn.edu.ept.postgram.shared.events.KafkaTopics;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final KafkaConfig.EventPublisher eventPublisher;

    public PostService(PostRepository postRepository, KafkaConfig.EventPublisher eventPublisher) {
        this.postRepository = postRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PostResponseDto createPost(PostRequestDto requestDto) {
        UUID authorId = getCurrentUserId();
        String authorUsername = getCurrentUsername();

        Post post = Post.builder()
                .authorId(authorId)
                .authorUsername(authorUsername)
                .content(requestDto.content())
                .mediaUrl(requestDto.mediaUrl())
                .visibility(requestDto.visibility())
                .build();

        Post savedPost = postRepository.save(post);

        // TODO: Create a PostPublishedEvent in shared module if needed, 
        // for now let's assume we use a generic one or send the object
        eventPublisher.publish(KafkaTopics.POST_PUBLISHED, savedPost.getId().toString(), savedPost.getId());

        return mapToResponseDto(savedPost);
    }

    public PostResponseDto getPostById(UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return mapToResponseDto(post);
    }

    @Transactional
    public PostResponseDto updatePost(UUID id, PostRequestDto requestDto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getAuthorId().equals(getCurrentUserId())) {
            throw new RuntimeException("Unauthorized to update this post");
        }

        post.setContent(requestDto.content());
        post.setMediaUrl(requestDto.mediaUrl());
        post.setVisibility(requestDto.visibility());

        return mapToResponseDto(postRepository.save(post));
    }

    @Transactional
    public void deletePost(UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getAuthorId().equals(getCurrentUserId())) {
            throw new RuntimeException("Unauthorized to delete this post");
        }

        postRepository.delete(post);
        eventPublisher.publish(KafkaTopics.POST_DELETED, id.toString(), id);
    }

    public List<PostResponseDto> getUserPosts(UUID userId) {
        return postRepository.findAllByAuthorIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private PostResponseDto mapToResponseDto(Post post) {
        return new PostResponseDto(
                post.getId(),
                post.getAuthorId(),
                post.getAuthorUsername(),
                post.getContent(),
                post.getMediaUrl(),
                post.getVisibility(),
                post.getCreatedAt(),
                post.getLikes().size(),
                post.getComments().size()
        );
    }

    private UUID getCurrentUserId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userIdStr = jwt.getClaimAsString("user_id");
        if (userIdStr == null) {
            // Fallback to subject if user_id claim is missing
            userIdStr = jwt.getSubject();
        }
        return UUID.fromString(userIdStr);
    }

    private String getCurrentUsername() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = jwt.getClaimAsString("preferred_username");
        if (username == null) {
            // Fallback to subject if preferred_username claim is missing
            username = jwt.getSubject();
        }
        return username;
    }
}
