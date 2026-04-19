package sn.edu.ept.postgram.socialservice.dto;

public record UpdateProfileRequest(
        String firstName,
        String lastName,
        String bio,
        String avatarUrl
) {}