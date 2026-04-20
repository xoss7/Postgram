package sn.edu.ept.postgram.feedservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sn.edu.ept.postgram.feedservice.dto.PostResponse;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "CONTENT-SERVICE")
public interface ContentServiceClient {

    @GetMapping("/posts/batch")
    List<PostResponse> getPostsByIds(@RequestParam List<UUID> ids);
}