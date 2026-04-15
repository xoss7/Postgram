package sn.edu.ept.postgram.shared.dto;

public record ProfileDto(
        String id,
        String firstName,
        String lastName,
        String username,
        String bio,
        String profileUrl
) {}
