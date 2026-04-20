package sn.edu.ept.postgram.contentservice.dto;

import sn.edu.ept.postgram.contentservice.entity.Post;
import sn.edu.ept.postgram.contentservice.model.Visibility;
import sn.edu.ept.postgram.contentservice.utils.MediaUrlResolver;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PostResponse(
        UUID id,
        UUID authorId,
        String content,
        Visibility visibility,
        List<PostMediaResponse> mediaFiles,
        int likesCount,
        int commentsCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PostResponse from(Post post, MediaUrlResolver resolver) {
        return new PostResponse(
                post.getId(),
                post.getAuthorId(),
                post.getContent(),
                post.getVisibility(),
                post.getMediaFiles().stream()
                        .map(m -> PostMediaResponse.from(m, resolver))
                        .toList(),
                post.getLikesCount(),
                post.getCommentsCount(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}