package sn.edu.ept.postgram.socialservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import sn.edu.ept.postgram.socialservice.dto.ProfileResponse;
import sn.edu.ept.postgram.socialservice.dto.UpdateProfileRequest;
import sn.edu.ept.postgram.socialservice.service.ProfileService;

import java.util.UUID;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = getUserId(jwt);
        return ResponseEntity.ok(profileService.getProfile(userId));
    }

    @GetMapping("/{username}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable String username) {
        return ResponseEntity.ok(profileService.getProfileByUsername(username));
    }

    @PatchMapping("/me")
    public ResponseEntity<ProfileResponse> updateProfile(
            @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = getUserId(jwt);
        return ResponseEntity.ok(profileService.updateProfile(userId, request));
    }

    private UUID getUserId(Jwt jwt) {
        return UUID.fromString(jwt.getClaim("user_id"));
    }
}