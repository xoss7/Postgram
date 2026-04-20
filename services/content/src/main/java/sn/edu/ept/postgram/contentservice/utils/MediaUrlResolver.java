package sn.edu.ept.postgram.contentservice.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MediaUrlResolver {

    @Value("${media.path:/api/v1/media}")
    private String mediaPath;

    public String resolve(UUID mediaId) {
        return mediaPath + "/" + mediaId;
    }
}