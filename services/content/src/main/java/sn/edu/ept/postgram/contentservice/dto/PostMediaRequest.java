package sn.edu.ept.postgram.contentservice.dto;

import jakarta.validation.constraints.NotNull;
import sn.edu.ept.postgram.contentservice.model.PostMediaType;

import java.util.UUID;

public record PostMediaRequest(
        @NotNull UUID mediaId,
        @NotNull PostMediaType mediaType
) {}