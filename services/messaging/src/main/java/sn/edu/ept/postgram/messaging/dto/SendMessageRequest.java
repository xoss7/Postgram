package sn.edu.ept.postgram.messaging.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

import java.util.UUID;

public record SendMessageRequest(
        @NonNull UUID receiverId,
        @NotBlank String content
        ) {}
