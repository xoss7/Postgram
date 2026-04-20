package sn.edu.ept.postgram.contentservice.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(
        @NotBlank String content
) {}
