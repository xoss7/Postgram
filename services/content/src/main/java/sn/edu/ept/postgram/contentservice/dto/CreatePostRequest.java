package sn.edu.ept.postgram.contentservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import sn.edu.ept.postgram.contentservice.model.Visibility;

import java.util.ArrayList;
import java.util.List;

@Builder
public record CreatePostRequest(
        String content,
        @NotNull Visibility visibility,
        List<PostMediaRequest> mediaFiles
) {
    public CreatePostRequest {
        if (mediaFiles == null) {
            mediaFiles = new ArrayList<>();
        }
    }
}