package sn.edu.ept.postgram.contentservice.dto;

import sn.edu.ept.postgram.contentservice.entity.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PostRequestDto(
    @NotBlank(message = "Content cannot be empty")
    String content,
    String mediaUrl,
    @NotNull(message = "Visibility is required")
    Visibility visibility
) {}
