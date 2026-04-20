package sn.edu.ept.postgram.socialservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sn.edu.ept.postgram.socialservice.entity.Follow;

import java.util.List;
import java.util.UUID;

@Repository
public interface FollowRepository extends JpaRepository<Follow, UUID> {

    boolean existsByFollowerIdAndFolloweeId(UUID followerId, UUID followeeId);

    void deleteByFollowerIdAndFolloweeId(UUID followerId, UUID followeeId);

    long countByFolloweeId(UUID followeeId);

    long countByFollowerId(UUID followerId);

    @Query("SELECT f.followerId FROM Follow f WHERE f.followeeId = :followeeId")
    List<UUID> findFollowerIdsByFolloweeId(UUID followeeId);

    @Query("SELECT f.followeeId FROM Follow f WHERE f.followerId = :followerId")
    List<UUID> findFolloweeIdsByFollowerId(UUID followerId);

}