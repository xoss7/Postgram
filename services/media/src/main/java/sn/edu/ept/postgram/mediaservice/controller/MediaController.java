package sn.edu.ept.postgram.mediaservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sn.edu.ept.postgram.mediaservice.dto.MediaFileResponseDto;
import sn.edu.ept.postgram.mediaservice.service.MediaService;

import java.util.UUID;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/avatar")
    public ResponseEntity<MediaFileResponseDto> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = UUID.fromString(jwt.getClaim("user_id").toString());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mediaService.uploadAvatar(userId, file));
    }

    @PostMapping("/posts")
    public ResponseEntity<MediaFileResponseDto> uploadPostMedia(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = UUID.fromString(jwt.getClaim("user_id").toString());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mediaService.uploadPostMedia(userId, file));
    }

    @DeleteMapping("/{mediaId}")
    public ResponseEntity<Void> deleteMedia(
            @PathVariable UUID mediaId,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = UUID.fromString(jwt.getClaim("user_id").toString());
        mediaService.delete(mediaId, userId);
        return ResponseEntity.noContent().build();
    }
}