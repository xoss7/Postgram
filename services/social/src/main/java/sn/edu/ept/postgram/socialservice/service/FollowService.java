package sn.edu.ept.postgram.socialservice.service;

import org.springframework.transaction.annotation.Transactional;
import sn.edu.ept.postgram.shared.events.KafkaTopics;
import sn.edu.ept.postgram.shared.events.UserFollowedEvent;
import sn.edu.ept.postgram.shared.events.UserUnfollowedEvent;
import sn.edu.ept.postgram.socialservice.dto.FollowStatsResponse;
import sn.edu.ept.postgram.socialservice.entity.Follow;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sn.edu.ept.postgram.socialservice.entity.Profile;
import sn.edu.ept.postgram.socialservice.kafka.EventPublisher;
import sn.edu.ept.postgram.socialservice.repository.FollowRepository;
import sn.edu.ept.postgram.socialservice.repository.ProfileRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowService {

    private final FollowRepository followRepository;
    private final ProfileRepository profileRepository;
    private final EventPublisher eventPublisher;

    public void follow(UUID followerId, UUID followeeId) {
        if (followerId.equals(followeeId)) {
            throw new RuntimeException("Cannot follow yourself");
        }
        if (followRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new RuntimeException("Already following");
        }
        if (!profileRepository.existsById(followeeId)) {
            throw new RuntimeException("User not found");
        }

        Follow follow = Follow.builder()
                .followerId(followerId)
                .followeeId(followeeId)
                .build();

        followRepository.save(follow);

        // récupérer le username du follower pour l'event
        Profile follower = profileRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        eventPublisher.publish(
                KafkaTopics.USER_FOLLOWED,
                followerId.toString(),
                new UserFollowedEvent(followerId, followeeId, follower.getUsername())
        );
    }

    public void unfollow(UUID followerId, UUID followeeId) {
        if (!followRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new RuntimeException("Not following");
        }
        followRepository.deleteByFollowerIdAndFolloweeId(followerId, followeeId);

        eventPublisher.publish(
                KafkaTopics.USER_UNFOLLOWED,
                followerId.toString(),
                new UserUnfollowedEvent(followerId, followeeId)
        );
    }

    @Transactional(readOnly = true)
    public FollowStatsResponse getStats(UUID userId) {
        return new FollowStatsResponse(
                followRepository.countByFolloweeId(userId),
                followRepository.countByFollowerId(userId)
        );
    }

    @Transactional(readOnly = true)
    public boolean isFollowing(UUID followerId, UUID followeeId) {
        return followRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
    }
}