package sn.edu.ept.postgram.socialservice;

import jakarta.transaction.Transactional;
import sn.edu.ept.postgram.socialservice.Follow;
import sn.edu.ept.postgram.socialservice.FollowRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final FollowEvent followEvent;


    @Transactional
    public void follow(UUID followerId, UUID followeeId){

        // ❌ self follow
        if (followerId.equals(followeeId)) {
            throw new RuntimeException("You cannot follow yourself");
        }

        // ❌ déjà follow
        if (followRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new RuntimeException("Already following");
        }

        Follow f = Follow.builder()
                .followerId(followerId)
                .followeeId(followeeId)
                .build();

        followRepository.save(f);

        // 🔥 EVENT KAFKA
        followEvent.sendFollowEvent(
                UserFollowedEvent.builder()
                        .followerId(followerId)
                        .followeeId(followeeId)
                        .build()
        );
    }

    @Transactional
    public void unfollow(UUID followerId, UUID followeeId){

        followRepository.deleteByFollowerIdAndFolloweeId(followerId, followeeId);

        // 🔥 EVENT KAFKA
        followEvent.sendUnfollowEvent(
                UserUnfollowedEvent.builder()
                        .followerId(followerId)
                        .followeeId(followeeId)
                        .build()
        );
    }

    public List<FollowResponse> getFollowers(UUID userId){
        return followRepository.findByFolloweeId(userId)
                .stream()
                .map(f -> new FollowResponse(f.getFollowerId(), f.getFolloweeId()))
                .collect(Collectors.toList());
    }

    public List<FollowResponse> getFollowing(UUID userId){
        return followRepository.findByFollowerId(userId)
                .stream()
                .map(f -> new FollowResponse(f.getFollowerId(), f.getFolloweeId()))
                .collect(Collectors.toList());
    }

    public List<FollowResponse> getAllFollows() {
        return followRepository.findAll()
                .stream()
                .map(f -> new FollowResponse(f.getFollowerId(), f.getFolloweeId()))
                .collect(Collectors.toList());
    }
}