package sn.edu.ept.postgram.contentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.edu.ept.postgram.contentservice.entity.Like;

import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, UUID> {

    boolean existsByPostIdAndUserId(UUID postId, UUID userId);

    void deleteByPostIdAndUserId(UUID postId, UUID userId);

    long countByPostId(UUID postId);
}