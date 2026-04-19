package sn.edu.ept.postgram.contentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sn.edu.ept.postgram.contentservice.entity.Visibility;

public record PostRequestDto(
        @NotBlank(message = "Content cannot be blank")
        String content,
        String mediaUrl,
        @NotNull(message = "Visibility is required")
        Visibility visibility
) {}
