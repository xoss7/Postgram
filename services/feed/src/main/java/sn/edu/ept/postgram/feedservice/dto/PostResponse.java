package sn.edu.ept.postgram.feedservice.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PostResponse(
        UUID id,
        UUID authorId,
        String content,
        String visibility,
        List<PostMediaResponse> mediaFiles,
        int likesCount,
        int commentsCount,
        LocalDateTime createdAt
) {}