package sn.edu.ept.postgram.feedservice.config;

import java.util.UUID;

public final class RedisKeys {
    private RedisKeys() {}

    public static String feed(UUID userId) {
        return "feed:" + userId;
    }
}