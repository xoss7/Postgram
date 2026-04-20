package sn.edu.ept.postgram.contentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sn.edu.ept.postgram.contentservice.config.EventPublisher;
import sn.edu.ept.postgram.contentservice.dto.CreatePostRequest;
import sn.edu.ept.postgram.contentservice.dto.PostMediaRequest;
import sn.edu.ept.postgram.contentservice.dto.PostResponse;
import sn.edu.ept.postgram.contentservice.dto.UpdatePostRequest;
import sn.edu.ept.postgram.contentservice.entity.Post;
import sn.edu.ept.postgram.contentservice.entity.PostMedia;
import sn.edu.ept.postgram.contentservice.repository.PostRepository;
import sn.edu.ept.postgram.contentservice.utils.MediaUrlResolver;
import sn.edu.ept.postgram.shared.events.KafkaTopics;
import sn.edu.ept.postgram.shared.events.PostDeletedEvent;
import sn.edu.ept.postgram.shared.events.PostPublishedEvent;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final EventPublisher eventPublisher;
    private final MediaUrlResolver mediaUrlResolver;

    public PostResponse createPost(UUID authorId, String authorUsername,
                                   CreatePostRequest request) {
        if (request.content() == null && request.mediaFiles().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post must have content or at least one media file");
        }

        Post post = Post.builder()
                .authorId(authorId)
                .content(request.content())
                .visibility(request.visibility())
                .build();

        IntStream.range(0, request.mediaFiles().size())
                .forEach(i -> {
                    PostMediaRequest m = request.mediaFiles().get(i);

                    PostMedia media = PostMedia.builder()
                            .mediaId(m.mediaId())
                            .mediaType(m.mediaType())
                            .displayOrder(i)
                            .build();

                    post.addMedia(media);
                });

        postRepository.save(post);

        eventPublisher.publish(
                KafkaTopics.POST_PUBLISHED,
                post.getId().toString(),
                new PostPublishedEvent(
                        post.getId(),
                        authorId,
                        authorUsername,
                        post.getVisibility().name(),
                        post.getCreatedAt()
                )
        );

        return PostResponse.from(post, mediaUrlResolver);
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        return PostResponse.from(post, mediaUrlResolver);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getUserPosts(UUID authorId, Pageable pageable) {
        return postRepository.findByAuthorIdOrderByCreatedAtDesc(authorId, pageable)
                .map(p -> PostResponse.from(p, mediaUrlResolver));
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByIds(List<UUID> ids) {
        return postRepository.findAllByIds(ids)
                .stream()
                .map(p -> PostResponse.from(p, mediaUrlResolver))
                .toList();
    }

    public PostResponse updatePost(UUID postId, UUID authorId, UpdatePostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (!post.getAuthorId().equals(authorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        if (request.content() != null) post.setContent(request.content());
        if (request.visibility() != null) post.setVisibility(request.visibility());

        return PostResponse.from(postRepository.save(post), mediaUrlResolver);
    }

    public void deletePost(UUID postId, UUID authorId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (!post.getAuthorId().equals(authorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        postRepository.delete(post);

        eventPublisher.publish(
                KafkaTopics.POST_DELETED,
                postId.toString(),
                new PostDeletedEvent(postId, authorId)
        );
    }
}