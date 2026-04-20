package sn.edu.ept.postgram.contentservice.dto;

import sn.edu.ept.postgram.contentservice.model.Visibility;

public record UpdatePostRequest(
        String content,
        Visibility visibility
) {}