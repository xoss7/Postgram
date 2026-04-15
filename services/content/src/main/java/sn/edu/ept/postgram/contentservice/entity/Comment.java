package sn.edu.ept.postgram.contentservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private UUID authorId;

    @Column(nullable = false)
    private String authorUsername;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public Comment() {}

    public Comment(UUID id, Post post, UUID authorId, String authorUsername, String content, LocalDateTime createdAt) {
        this.id = id;
        this.post = post;
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.content = content;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
    public UUID getAuthorId() { return authorId; }
    public void setAuthorId(UUID authorId) { this.authorId = authorId; }
    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static CommentBuilder builder() {
        return new CommentBuilder();
    }

    public static class CommentBuilder {
        private UUID id;
        private Post post;
        private UUID authorId;
        private String authorUsername;
        private String content;
        private LocalDateTime createdAt;

        public CommentBuilder id(UUID id) { this.id = id; return this; }
        public CommentBuilder post(Post post) { this.post = post; return this; }
        public CommentBuilder authorId(UUID authorId) { this.authorId = authorId; return this; }
        public CommentBuilder authorUsername(String authorUsername) { this.authorUsername = authorUsername; return this; }
        public CommentBuilder content(String content) { this.content = content; return this; }
        public CommentBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Comment build() {
            return new Comment(id, post, authorId, authorUsername, content, createdAt);
        }
    }
}
