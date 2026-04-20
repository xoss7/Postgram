package sn.edu.ept.postgram.mediaservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sn.edu.ept.postgram.mediaservice.dto.MediaFileResponseDto;
import sn.edu.ept.postgram.mediaservice.service.MediaService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/avatar")
    public ResponseEntity<Map<String, Object>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = UUID.fromString(jwt.getClaim("user_id").toString());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mediaService.uploadAvatar(userId, file));
    }

    @PostMapping("/posts")
    public ResponseEntity<Map<String, Object>> uploadPostMedia(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = UUID.fromString(jwt.getClaim("user_id").toString());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mediaService.uploadPostMedia(userId, file));
    }

    @GetMapping("/{mediaId}")
    public ResponseEntity<Resource> getMedia(@PathVariable UUID mediaId) {
        MediaFileResponseDto file = mediaService.getMedia(mediaId);

        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(file.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.filename() + "\"")
                .body(file.resource());
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