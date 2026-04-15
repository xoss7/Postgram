package sn.edu.ept.postgram.contentservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "likes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"post_id", "userId"})
})
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String username;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public Like() {}

    public Like(UUID id, Post post, UUID userId, String username, LocalDateTime createdAt) {
        this.id = id;
        this.post = post;
        this.userId = userId;
        this.username = username;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static LikeBuilder builder() {
        return new LikeBuilder();
    }

    public static class LikeBuilder {
        private UUID id;
        private Post post;
        private UUID userId;
        private String username;
        private LocalDateTime createdAt;

        public LikeBuilder id(UUID id) { this.id = id; return this; }
        public LikeBuilder post(Post post) { this.post = post; return this; }
        public LikeBuilder userId(UUID userId) { this.userId = userId; return this; }
        public LikeBuilder username(String username) { this.username = username; return this; }
        public LikeBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Like build() {
            return new Like(id, post, userId, username, createdAt);
        }
    }
}
