package sn.edu.ept.postgram.contentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sn.edu.ept.postgram.contentservice.config.EventPublisher;
import sn.edu.ept.postgram.contentservice.dto.PostRequestDto;
import sn.edu.ept.postgram.contentservice.dto.PostResponseDto;
import sn.edu.ept.postgram.contentservice.entity.Post;
import sn.edu.ept.postgram.contentservice.repository.PostRepository;
import sn.edu.ept.postgram.shared.events.KafkaTopics;
import sn.edu.ept.postgram.shared.events.PostDeletedEvent;
import sn.edu.ept.postgram.shared.events.PostPublishedEvent;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public PostResponseDto createPost(PostRequestDto requestDto) {
        Post post = Post.builder()
                .authorId(CurrentUserClaims.userId())
                .authorUsername(CurrentUserClaims.username())
                .content(requestDto.content())
                .mediaUrl(requestDto.mediaUrl())
                .visibility(requestDto.visibility())
                .build();

        Post savedPost = postRepository.save(post);

        PostPublishedEvent event = new PostPublishedEvent(
                savedPost.getId(),
                savedPost.getAuthorId(),
                savedPost.getAuthorUsername(),
                savedPost.getContent(),
                savedPost.getMediaUrl(),
                savedPost.getVisibility().name()
        );

        eventPublisher.publish(KafkaTopics.POST_PUBLISHED, savedPost.getId().toString(), event);

        return mapToResponseDto(savedPost);
    }

    public PostResponseDto getPost(UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        return mapToResponseDto(post);
    }

    public List<PostResponseDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PostResponseDto updatePost(UUID id, PostRequestDto requestDto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (!post.getAuthorId().equals(CurrentUserClaims.userId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own posts");
        }

        post.setContent(requestDto.content());
        post.setMediaUrl(requestDto.mediaUrl());
        post.setVisibility(requestDto.visibility());

        return mapToResponseDto(postRepository.save(post));
    }

    @Transactional
    public void deletePost(UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (!post.getAuthorId().equals(CurrentUserClaims.userId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own posts");
        }

        postRepository.delete(post);

        PostDeletedEvent event = new PostDeletedEvent(
                id,
                post.getAuthorId()
        );

        eventPublisher.publish(KafkaTopics.POST_DELETED, id.toString(), event);
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
                post.getUpdatedAt(),
                post.getLikes().size(),
                post.getComments().size()
        );
    }
}
