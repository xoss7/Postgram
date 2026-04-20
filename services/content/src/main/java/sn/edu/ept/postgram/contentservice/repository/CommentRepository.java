package sn.edu.ept.postgram.contentservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.edu.ept.postgram.contentservice.entity.Comment;

import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    Page<Comment> findByPostIdOrderByCreatedAtDesc(UUID postId, Pageable pageable);

    long countByPostId(UUID postId);
}