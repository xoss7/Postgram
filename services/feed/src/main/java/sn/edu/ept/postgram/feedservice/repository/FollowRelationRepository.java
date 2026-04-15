package sn.edu.ept.postgram.feedservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.edu.ept.postgram.feedservice.model.FollowRelationEntity;

import java.util.List;
import java.util.UUID;

public interface FollowRelationRepository extends JpaRepository<FollowRelationEntity, FollowRelationEntity.FollowRelationId> {

    List<FollowRelationEntity> findByFolloweeId(UUID followeeId);

    List<FollowRelationEntity> findByFollowerId(UUID followerId);

    boolean existsByFollowerIdAndFolloweeId(UUID followerId, UUID followeeId);
}
