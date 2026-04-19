package sn.edu.ept.postgram.contentservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sn.edu.ept.postgram.contentservice.service.LikeService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts/{postId}/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void likePost(@PathVariable UUID postId) {
        likeService.likePost(postId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlikePost(@PathVariable UUID postId) {
        likeService.unlikePost(postId);
    }
}
