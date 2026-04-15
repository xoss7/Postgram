package sn.edu.ept.postgram.feedservice.dto.api;

import java.util.List;

public record FeedResponse(
        List<FeedItemResponse> items,
        int offset,
        int limit,
        int returnedCount,
        boolean hasMore
) {
}
