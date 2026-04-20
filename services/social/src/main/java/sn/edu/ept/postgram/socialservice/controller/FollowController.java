package sn.edu.ept.postgram.socialservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import sn.edu.ept.postgram.socialservice.dto.FollowStatsResponse;
import sn.edu.ept.postgram.socialservice.service.FollowService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{followeeId}")
    public ResponseEntity<Void> follow(@PathVariable UUID followeeId,
                                       @AuthenticationPrincipal Jwt jwt) {
        UUID followerId = UUID.fromString(jwt.getClaim("user_id"));
        followService.follow(followerId, followeeId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{followeeId}")
    public ResponseEntity<Void> unfollow(@PathVariable UUID followeeId,
                                         @AuthenticationPrincipal Jwt jwt) {
        UUID followerId = UUID.fromString(jwt.getClaim("user_id"));
        followService.unfollow(followerId, followeeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UUID>> getFollowers(@PathVariable UUID userId) {
        return ResponseEntity.ok(followService.getFollowerIds(userId));
    }

    @GetMapping("/{userId}/followees")
    public ResponseEntity<List<UUID>> getFollowees(@PathVariable UUID userId) {
        return ResponseEntity.ok(followService.getFolloweeIds(userId));
    }

    @GetMapping("/{userId}/stats")
    public ResponseEntity<FollowStatsResponse> getStats(@PathVariable UUID userId) {
        return ResponseEntity.ok(followService.getStats(userId));
    }

    @GetMapping("/{followeeId}/is-following")
    public ResponseEntity<Map<String, Boolean>> isFollowing(
            @PathVariable UUID followeeId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID followerId = UUID.fromString(jwt.getClaim("user_id"));
        return ResponseEntity.ok(
                Map.of("following", followService.isFollowing(followerId, followeeId))
        );
    }
}