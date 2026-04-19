package sn.edu.ept.postgram.socialservice.dto;

import sn.edu.ept.postgram.socialservice.entity.Profile;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProfileResponse(
        UUID id,
        String username,
        String email,
        String firstName,
        String lastName,
        String bio,
        String avatarUrl,
        LocalDateTime createdAt
) {
    public static ProfileResponse from(Profile p) {
        return new ProfileResponse(
                p.getId(),
                p.getUsername(),
                p.getEmail(),
                p.getFirstName(),
                p.getLastName(),
                p.getBio(),
                p.getAvatarUrl(),
                p.getCreatedAt()
        );
    }
}
