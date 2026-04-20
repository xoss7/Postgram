package sn.edu.ept.postgram.contentservice.dto;


import sn.edu.ept.postgram.contentservice.entity.PostMedia;
import sn.edu.ept.postgram.contentservice.model.PostMediaType;
import sn.edu.ept.postgram.contentservice.utils.MediaUrlResolver;

import java.util.UUID;

public record PostMediaResponse(
        UUID id,
        String mediaUrl,
        PostMediaType mediaType,
        int displayOrder
) {
    public static PostMediaResponse from(PostMedia m, MediaUrlResolver resolver) {
        return new PostMediaResponse(
                m.getId(),
                resolver.resolve(m.getMediaId()),
                m.getMediaType(),
                m.getDisplayOrder()
        );
    }
}