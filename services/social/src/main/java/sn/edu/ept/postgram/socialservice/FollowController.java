package sn.edu.ept.postgram.socialservice;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/social")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService service;

    // FOLLOW
    @PostMapping("/follow")
    public void follow(@RequestBody FollowDto request) {
        service.follow(request.getFollowerId(), request.getFolloweeId());
    }

    // UNFOLLOW (REST propre)
    @DeleteMapping("/{followerId}/unfollow/{followeeId}")
    public void unfollow(@PathVariable UUID followerId,
                         @PathVariable UUID followeeId) {
        service.unfollow(followerId, followeeId);
    }

    // ALL
    @GetMapping("/follows")
    public List<FollowResponse> getAllFollows() {
        return service.getAllFollows();
    }

    // FOLLOWERS
    @GetMapping("/{userId}/followers")
    public List<FollowResponse> getFollowers(@PathVariable UUID userId) {
        return service.getFollowers(userId);
    }

    // FOLLOWING
    @GetMapping("/{userId}/following")
    public List<FollowResponse> getFollowing(@PathVariable UUID userId) {
        return service.getFollowing(userId);
    }
}