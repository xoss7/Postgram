package sn.edu.ept.postgram.contentservice.dto;

import sn.edu.ept.postgram.contentservice.entity.Visibility;
import java.time.LocalDateTime;
import java.util.UUID;

public record PostResponseDto(
    UUID id,
    UUID authorId,
    String authorUsername,
    String content,
    String mediaUrl,
    Visibility visibility,
    LocalDateTime createdAt,
    long likesCount,
    long commentsCount
) {}
