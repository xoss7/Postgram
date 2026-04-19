package sn.edu.ept.postgram.mediaservice.dto;

import org.springframework.core.io.Resource;

public record MediaFileResponseDto(
        Resource resource,
        String contentType,
        String filename
) {}