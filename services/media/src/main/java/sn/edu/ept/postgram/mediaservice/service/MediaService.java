package sn.edu.ept.postgram.mediaservice.service;

import io.minio.*;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sn.edu.ept.postgram.mediaservice.dto.MediaFileResponseDto;
import sn.edu.ept.postgram.mediaservice.entity.Media;
import sn.edu.ept.postgram.mediaservice.model.MediaType;
import sn.edu.ept.postgram.mediaservice.repository.MediaRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;
    private final MinioClient minioClient;

    @Value("${app.minio.buckets.avatars}")
    private String avatarsBucket;

    @Value("${app.minio.buckets.posts}")
    private String postsBucket;

    @Value("${app.minio.endpoint}")
    private String minioEndpoint;

    @Value("${app.file-size.avatar}")
    private Long avatarSize;

    @Value("${app.file-size.media}")
    private Long mediaSize;

    @PostConstruct
    public void initBuckets() {
        createBucketIfNotExists(avatarsBucket);
        createBucketIfNotExists(postsBucket);
    }

    public MediaFileResponseDto uploadAvatar(UUID uploaderId, MultipartFile file) {
        validateImage(file);

        // delete former avater if exists
        mediaRepository.findByUploaderIdAndType(uploaderId, MediaType.AVATAR)
                .ifPresent(existing -> {
                    deleteFromMinio(existing.getBucket(), existing.getFilename());
                    mediaRepository.delete(existing);
                });
        return upload(uploaderId, file, MediaType.AVATAR, avatarsBucket);
    }

    public MediaFileResponseDto uploadPostMedia(UUID uploaderId, MultipartFile file) {
        validateMedia(file);
        return upload(uploaderId, file, detectMediaType(file), postsBucket);
    }

    public void delete(UUID mediaId, UUID requesterId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new EntityNotFoundException("Media Not Found"));

        if (!media.getUploaderId().equals(requesterId)) {
            throw new RuntimeException("Forbidden");
        }

        deleteFromMinio(media.getBucket(), media.getFilename());
        mediaRepository.delete(media);
    }

    private MediaFileResponseDto upload(UUID uploaderId, MultipartFile file,
                                        MediaType mediaType, String bucket) {
        String filename = generateFilename(uploaderId, file);

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(filename)
                            .stream(file.getInputStream(), file.getSize(), -1L)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }

        String url = buildUrl(bucket, filename);

        Media media = Media.builder()
                .uploaderId(uploaderId)
                .type(mediaType)
                .bucket(bucket)
                .filename(filename)
                .url(url)
                .contentType(file.getContentType())
                .size(file.getSize())
                .build();
        mediaRepository.save(media);
        return new MediaFileResponseDto(media.getId(), url, mediaType);
    }

    private void deleteFromMinio(String bucket, String filename) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(filename)
                            .build()
            );
        } catch (Exception ex) {
            log.error("Failed to delete file {} from MinIO : {}", filename, ex.getMessage());
        }
    }

    private void createBucketIfNotExists(String bucketName) {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Bucket '{}' created", bucketName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialized bucket: " + bucketName, e);
        }
    }

    private String generateFilename(UUID uploaderId, MultipartFile file) {
        String extension = getExtension(file.getOriginalFilename());
        return uploaderId + "/" + UUID.randomUUID() + "." + extension;
    }

    private String buildUrl(String bucket, String filename) {
        return minioEndpoint + "/" + bucket + "/" + filename;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "bin";
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    private MediaType detectMediaType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null && contentType.startsWith("video/")) return MediaType.POST_VIDEO;
        return MediaType.POST_IMAGE;
    }

    private void validateImage(MultipartFile file) {
        String ct = file.getContentType();

        if (ct == null || !ct.startsWith("image/")) {
            throw new RuntimeException("Only images are allowed for avatars");
        }
        if (file.getSize() > avatarSize) {
            throw new RuntimeException("Avatar must be under 5MB");
        }
    }

    private void validateMedia(MultipartFile file) {
        String ct = file.getContentType();

        if (ct == null || (!ct.startsWith("image/")) && !ct.startsWith("video/")) {
            throw new RuntimeException("Only images and videos are allowed");
        }
        if (file.getSize() > mediaSize) {
            throw new RuntimeException("File must be under 50MB");
        }
    }
}