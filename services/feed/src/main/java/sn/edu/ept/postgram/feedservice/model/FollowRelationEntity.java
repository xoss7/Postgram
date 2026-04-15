package sn.edu.ept.postgram.feedservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "follows")
@IdClass(FollowRelationEntity.FollowRelationId.class)
public class FollowRelationEntity {

    @Id
    @Column(name = "follower_id", nullable = false)
    private UUID followerId;

    @Id
    @Column(name = "followee_id", nullable = false)
    private UUID followeeId;

    public FollowRelationEntity() {
    }

    public FollowRelationEntity(UUID followerId, UUID followeeId) {
        this.followerId = followerId;
        this.followeeId = followeeId;
    }

    public UUID getFollowerId() {
        return followerId;
    }

    public UUID getFolloweeId() {
        return followeeId;
    }

    public static final class FollowRelationId implements Serializable {
        private UUID followerId;
        private UUID followeeId;

        public FollowRelationId() {
        }

        public FollowRelationId(UUID followerId, UUID followeeId) {
            this.followerId = followerId;
            this.followeeId = followeeId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof FollowRelationId that)) {
                return false;
            }
            return Objects.equals(followerId, that.followerId) && Objects.equals(followeeId, that.followeeId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(followerId, followeeId);
        }
    }
}
