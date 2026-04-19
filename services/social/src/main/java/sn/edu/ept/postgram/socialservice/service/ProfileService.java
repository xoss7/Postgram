package sn.edu.ept.postgram.socialservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.edu.ept.postgram.shared.events.UserRegisteredEvent;
import sn.edu.ept.postgram.socialservice.dto.ProfileResponse;
import sn.edu.ept.postgram.socialservice.dto.UpdateProfileRequest;
import sn.edu.ept.postgram.socialservice.entity.Profile;
import sn.edu.ept.postgram.socialservice.repository.ProfileRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;

    public void createProfile(UserRegisteredEvent event) {
        if (profileRepository.existsById(event.userId())) return;  // idempotent

        Profile profile = Profile.builder()
                .id(event.userId())
                .firstName(event.firstName())
                .lastName(event.lastName())
                .username(event.username())
                .email(event.email())
                .bio(event.bio())
                .build();

        profileRepository.save(profile);
        log.info("Profile created for user {}", event.username());
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(UUID userId) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return ProfileResponse.from(profile);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfileByUsername(String username) {
        Profile profile = profileRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return ProfileResponse.from(profile);
    }

    public ProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        if (request.firstName() != null) profile.setFirstName(request.firstName());
        if (request.lastName() != null) profile.setLastName(request.lastName());
        if (request.bio() != null) profile.setBio(request.bio());
        if (request.avatarUrl() != null) profile.setAvatarUrl(request.avatarUrl());

        return ProfileResponse.from(profileRepository.save(profile));
    }
}