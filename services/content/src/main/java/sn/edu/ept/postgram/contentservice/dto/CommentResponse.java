package sn.edu.ept.postgram.contentservice.dto;

import sn.edu.ept.postgram.contentservice.entity.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID postId,
        UUID authorId,
        String authorUsername,
        String content,
        LocalDateTime createdAt
) {
    public static CommentResponse from(Comment c) {
        return new CommentResponse(
                c.getId(),
                c.getPostId(),
                c.getAuthorId(),
                c.getAuthorUsername(),
                c.getContent(),
                c.getCreatedAt()
        );
    }
}