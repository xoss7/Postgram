package sn.edu.ept.postgram.socialservice;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowEvent {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendFollowEvent(UserFollowedEvent event) {
        kafkaTemplate.send("user-followed", event);
    }

    public void sendUnfollowEvent(UserUnfollowedEvent event) {
        kafkaTemplate.send("user-unfollowed", event);
    }
}