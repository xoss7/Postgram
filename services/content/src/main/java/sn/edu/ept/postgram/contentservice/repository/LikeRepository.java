package sn.edu.ept.postgram.contentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.edu.ept.postgram.contentservice.entity.Like;
import sn.edu.ept.postgram.contentservice.entity.Post;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, UUID> {
    Optional<Like> findByPostAndUserId(Post post, UUID userId);
    Optional<Like> findByPostIdAndUserId(UUID postId, UUID userId);
    boolean existsByPostAndUserId(Post post, UUID userId);
    void deleteByPostAndUserId(Post post, UUID userId);
}
