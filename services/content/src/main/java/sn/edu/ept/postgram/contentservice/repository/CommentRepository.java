package sn.edu.ept.postgram.contentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.edu.ept.postgram.contentservice.entity.Comment;
import sn.edu.ept.postgram.contentservice.entity.Post;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findAllByPostOrderByCreatedAtAsc(Post post);
    List<Comment> findAllByPostIdOrderByCreatedAtAsc(UUID postId);
}
