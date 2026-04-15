package sn.edu.ept.postgram.contentservice.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentRequestDto(
    @NotBlank(message = "Comment content cannot be empty")
    String content
) {}
