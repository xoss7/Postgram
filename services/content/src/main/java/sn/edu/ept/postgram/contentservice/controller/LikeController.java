package sn.edu.ept.postgram.contentservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.edu.ept.postgram.contentservice.service.LikeService;

import java.util.UUID;

@RestController
@RequestMapping("/content/posts/{postId}")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/like")
    public ResponseEntity<Void> likePost(@PathVariable UUID postId) {
        likeService.likePost(postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unlike")
    public ResponseEntity<Void> unlikePost(@PathVariable UUID postId) {
        likeService.unlikePost(postId);
        return ResponseEntity.ok().build();
    }
}
