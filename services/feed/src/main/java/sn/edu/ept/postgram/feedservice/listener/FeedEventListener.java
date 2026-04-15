package sn.edu.ept.postgram.feedservice.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import sn.edu.ept.postgram.feedservice.dto.event.PostDeletedEvent;
import sn.edu.ept.postgram.feedservice.dto.event.PostInteractionEvent;
import sn.edu.ept.postgram.feedservice.dto.event.PostPublishedEvent;
import sn.edu.ept.postgram.feedservice.dto.event.UserFollowedEvent;
import sn.edu.ept.postgram.feedservice.dto.event.UserUnfollowedEvent;
import sn.edu.ept.postgram.feedservice.service.FeedService;

@Component
public class FeedEventListener {

    private final FeedService feedService;

    public FeedEventListener(FeedService feedService) {
        this.feedService = feedService;
    }

    @KafkaListener(
            topics = "post-published",
            containerFactory = "postPublishedKafkaListenerContainerFactory"
    )
    public void onPostPublished(PostPublishedEvent event) {
        System.out.println("Received PostPublishedEvent: " + event);
        feedService.handlePostPublished(event);
    }

    @KafkaListener(
            topics = {"post-liked", "post-unliked"},
            containerFactory = "postInteractionKafkaListenerContainerFactory"
    )
    public void onPostInteraction(PostInteractionEvent event) {
        System.out.println("Received PostInteractionEvent: " + event);
        feedService.handlePostInteraction(event);
    }

    @KafkaListener(
            topics = "user-followed",
            containerFactory = "userFollowedKafkaListenerContainerFactory"
    )
    public void onUserFollowed(UserFollowedEvent event) {
        System.out.println("Received UserFollowedEvent: " + event);
        feedService.handleUserFollowed(event);
    }

    @KafkaListener(
            topics = "user-unfollowed",
            containerFactory = "userUnfollowedKafkaListenerContainerFactory"
    )
    public void onUserUnfollowed(UserUnfollowedEvent event) {
        System.out.println("Received UserUnfollowedEvent: " + event);
        feedService.handleUserUnfollowed(event);
    }

    @KafkaListener(
            topics = "post-deleted",
            containerFactory = "postDeletedKafkaListenerContainerFactory"
    )
    public void onPostDeleted(PostDeletedEvent event) {
        feedService.handlePostDeleted(event);
    }
}
