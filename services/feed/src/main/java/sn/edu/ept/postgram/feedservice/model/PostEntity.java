package sn.edu.ept.postgram.feedservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "posts")
public class PostEntity {

    @Id
    private UUID id;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Column(name = "caption")
    private String caption;

    @Column(name = "media_url")
    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostVisibility visibility;

    @Column(name = "likes_count", nullable = false)
    private long likesCount;

    @Column(name = "comments_count", nullable = false)
    private long commentsCount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    public UUID getId() {
        return id;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public String getCaption() {
        return caption;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public PostVisibility getVisibility() {
        return visibility;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public long getCommentsCount() {
        return commentsCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isDeleted() {
        return deleted;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public void setAuthorId(UUID authorId) {
        this.authorId = authorId;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public void setVisibility(PostVisibility visibility) {
        this.visibility = visibility;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }

    public void setCommentsCount(long commentsCount) {
        this.commentsCount = commentsCount;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
