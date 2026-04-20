package sn.edu.ept.postgram.feedservice.dto;

import java.util.List;

public record FeedResponse(
        List<PostResponse> posts,
        int page,
        int size,
        long total
) {}