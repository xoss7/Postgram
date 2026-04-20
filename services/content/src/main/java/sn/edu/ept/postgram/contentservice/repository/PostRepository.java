package sn.edu.ept.postgram.contentservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sn.edu.ept.postgram.contentservice.entity.Post;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    Page<Post> findByAuthorIdOrderByCreatedAtDesc(UUID authorId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.id IN :ids")
    List<Post> findAllByIds(List<UUID> ids);
}