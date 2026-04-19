package sn.edu.ept.postgram.socialservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import sn.edu.ept.postgram.shared.events.KafkaTopics;
import sn.edu.ept.postgram.shared.events.UserRegisteredEvent;
import sn.edu.ept.postgram.socialservice.service.ProfileService;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final ProfileService profileService;

    @KafkaListener(topics = KafkaTopics.USER_REGISTERED, groupId = "social-group")
    public void onUserRegistered(@Payload UserRegisteredEvent event) {
        log.info("Received UserRegisteredEvent for user {}", event.username());
        profileService.createProfile(event);
    }
}