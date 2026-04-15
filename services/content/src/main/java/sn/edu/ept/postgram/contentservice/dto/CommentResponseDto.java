package sn.edu.ept.postgram.contentservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentResponseDto(
    UUID id,
    UUID postId,
    UUID authorId,
    String authorUsername,
    String content,
    LocalDateTime createdAt
) {}
