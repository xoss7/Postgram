package sn.edu.ept.postgram.feedservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "SOCIAL-SERVICE")
public interface SocialServiceClient {

    @GetMapping("/follows/{userId}/followers")
    List<UUID> getFollowers(@PathVariable UUID userId);

    @GetMapping("/follows/{userId}/followees")
    List<UUID> getFollowees(@PathVariable UUID userId);
}