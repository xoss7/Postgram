package sn.edu.ept.postgram.socialservice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, UUID> {
    List<Follow> findByFollowerId(UUID followerId);
    List<Follow> findByFolloweeId(UUID followeeId);
    void deleteByFollowerIdAndFolloweeId(UUID followerId, UUID followeeId);
    boolean existsByFollowerIdAndFolloweeId(UUID followerId, UUID followeeId);

}