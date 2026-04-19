package sn.edu.ept.postgram.socialservice.dto;

public record FollowStatsResponse(
        long followers,
        long following
) {}