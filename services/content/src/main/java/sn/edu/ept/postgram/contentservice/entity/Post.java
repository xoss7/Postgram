package sn.edu.ept.postgram.contentservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID authorId;

    @Column(nullable = false)
    private String authorUsername;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility = Visibility.PUBLIC;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    public Post() {}

    public Post(UUID id, UUID authorId, String authorUsername, String content, String mediaUrl, Visibility visibility, LocalDateTime createdAt, LocalDateTime updatedAt, List<Comment> comments, List<Like> likes) {
        this.id = id;
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.content = content;
        this.mediaUrl = mediaUrl;
        this.visibility = visibility;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.comments = comments;
        this.likes = likes;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAuthorId() { return authorId; }
    public void setAuthorId(UUID authorId) { this.authorId = authorId; }
    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public Visibility getVisibility() { return visibility; }
    public void setVisibility(Visibility visibility) { this.visibility = visibility; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
    public List<Like> getLikes() { return likes; }
    public void setLikes(List<Like> likes) { this.likes = likes; }

    public static PostBuilder builder() {
        return new PostBuilder();
    }

    public static class PostBuilder {
        private UUID id;
        private UUID authorId;
        private String authorUsername;
        private String content;
        private String mediaUrl;
        private Visibility visibility;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<Comment> comments = new ArrayList<>();
        private List<Like> likes = new ArrayList<>();

        public PostBuilder id(UUID id) { this.id = id; return this; }
        public PostBuilder authorId(UUID authorId) { this.authorId = authorId; return this; }
        public PostBuilder authorUsername(String authorUsername) { this.authorUsername = authorUsername; return this; }
        public PostBuilder content(String content) { this.content = content; return this; }
        public PostBuilder mediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; return this; }
        public PostBuilder visibility(Visibility visibility) { this.visibility = visibility; return this; }
        public PostBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public PostBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public PostBuilder comments(List<Comment> comments) { this.comments = comments; return this; }
        public PostBuilder likes(List<Like> likes) { this.likes = likes; return this; }

        public Post build() {
            return new Post(id, authorId, authorUsername, content, mediaUrl, visibility, createdAt, updatedAt, comments, likes);
        }
    }
}
