package sn.edu.ept.postgram.contentservice.utils;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

public final class CurrentUserClaims {

    private CurrentUserClaims() {
    }

    public static UUID userId() {
        Jwt jwt = currentJwt();
        String userIdStr = jwt.getClaimAsString("user_id");
        if (userIdStr == null || userIdStr.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing claim: user_id");
        }

        try {
            return UUID.fromString(userIdStr);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid claim format: user_id");
        }
    }

    public static String username() {
        Jwt jwt = currentJwt();
        String username = jwt.getSubject();
        if (username == null || username.isBlank()) {
            username = jwt.getSubject();
        }
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing principal username");
        }
        return username;
    }

    private static Jwt currentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing JWT authentication");
        }
        return jwt;
    }
}
