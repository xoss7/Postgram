package sn.edu.ept.postgram.feedservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sn.edu.ept.postgram.feedservice.dto.FeedResponse;
import sn.edu.ept.postgram.feedservice.service.FeedService;

import java.util.UUID;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public ResponseEntity<FeedResponse> getFeed(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        UUID userId = UUID.fromString(jwt.getClaim("user_id"));
        return ResponseEntity.ok(feedService.getFeed(userId, page, size));
    }
}