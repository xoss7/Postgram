package sn.edu.ept.postgram.mediaservice.dto;

import sn.edu.ept.postgram.mediaservice.model.MediaType;

import java.util.UUID;

public record MediaFileResponseDto(
        UUID id,
        String url,
        MediaType type
) {}