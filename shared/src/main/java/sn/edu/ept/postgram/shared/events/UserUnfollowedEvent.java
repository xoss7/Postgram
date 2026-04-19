package sn.edu.ept.postgram.shared.events;

import java.util.UUID;

public record UserUnfollowedEvent(
    UUID followerId,
    UUID followeeId
) {}