package sn.edu.ept.postgram.contentservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sn.edu.ept.postgram.contentservice.dto.PostRequestDto;
import sn.edu.ept.postgram.contentservice.dto.PostResponseDto;
import sn.edu.ept.postgram.contentservice.service.PostService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/health")
    public String health() {
        return "Content Service is up!";
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponseDto createPost(@Valid @RequestBody PostRequestDto requestDto) {
        return postService.createPost(requestDto);
    }

    @GetMapping("/{id}")
    public PostResponseDto getPost(@PathVariable UUID id) {
        return postService.getPost(id);
    }

    @GetMapping
    public List<PostResponseDto> getAllPosts() {
        return postService.getAllPosts();
    }

    @PutMapping("/{id}")
    public PostResponseDto updatePost(@PathVariable UUID id, @Valid @RequestBody PostRequestDto requestDto) {
        return postService.updatePost(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable UUID id) {
        postService.deletePost(id);
    }
}
