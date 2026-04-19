package sn.edu.ept.postgram.contentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.edu.ept.postgram.contentservice.entity.Like;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, UUID> {
    Optional<Like> findByPostIdAndAuthorId(UUID postId, UUID authorId);
    boolean existsByPostIdAndAuthorId(UUID postId, UUID authorId);
    void deleteByPostIdAndAuthorId(UUID postId, UUID authorId);
}
