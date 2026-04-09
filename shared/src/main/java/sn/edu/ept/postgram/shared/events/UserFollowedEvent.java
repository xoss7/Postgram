package sn.edu.ept.postgram.shared.events;

import java.util.UUID;

public record UserFollowedEvent(
        UUID followeeId,
        UUID followerId,
        String followerUsername
) {}
