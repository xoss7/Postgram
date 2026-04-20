package sn.edu.ept.postgram.feedservice.dto;

import java.util.UUID;

public record PostMediaResponse(
        UUID id,
        String mediaUrl,
        String mediaType,
        int displayOrder
) {}