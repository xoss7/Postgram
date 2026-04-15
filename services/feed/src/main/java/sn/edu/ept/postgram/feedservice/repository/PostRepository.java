package sn.edu.ept.postgram.feedservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sn.edu.ept.postgram.feedservice.model.PostEntity;
import sn.edu.ept.postgram.feedservice.model.PostVisibility;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<PostEntity, UUID> {

    Optional<PostEntity> findByIdAndDeletedFalse(UUID id);

    List<PostEntity> findByIdInAndDeletedFalse(Collection<UUID> ids);

    List<PostEntity> findByAuthorIdAndDeletedFalse(UUID authorId);

    @Query("""
            select p
            from PostEntity p
            where p.authorId = :authorId
              and p.deleted = false
              and p.visibility <> :excludedVisibility
            order by p.createdAt desc
            """)
    List<PostEntity> findRecentVisiblePostsByAuthor(
            @Param("authorId") UUID authorId,
            @Param("excludedVisibility") PostVisibility excludedVisibility,
            Pageable pageable
    );

    @Query("""
            select p
            from PostEntity p
            where p.deleted = false
              and (p.authorId = :viewerId or p.authorId in :followeeIds)
              and (
                  p.visibility = sn.edu.ept.postgram.feedservice.model.PostVisibility.PUBLIC
                  or p.visibility = sn.edu.ept.postgram.feedservice.model.PostVisibility.FOLLOWERS_ONLY
                  or (p.visibility = sn.edu.ept.postgram.feedservice.model.PostVisibility.PRIVATE and p.authorId = :viewerId)
              )
            order by p.createdAt desc
            """)
    List<PostEntity> findFeedCandidates(
            @Param("viewerId") UUID viewerId,
            @Param("followeeIds") Collection<UUID> followeeIds,
            Pageable pageable
    );
}
