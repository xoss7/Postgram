package sn.edu.ept.postgram.feedservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import sn.edu.ept.postgram.feedservice.service.FeedFanoutService;
import sn.edu.ept.postgram.shared.events.*;

import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedEventConsumer {

    private final FeedFanoutService fanoutService;

    @KafkaListener(topics = KafkaTopics.POST_PUBLISHED, groupId = "feed-group")
    public void onPostPublished(@Payload PostPublishedEvent event) {
        log.info("PostPublished: {}", event.postId());
        if ("PRIVATE".equals(event.visibility())) return; // pas dans le feed

        long score = event.createdAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        fanoutService.fanoutPost(event.postId(), event.authorId(), score);
    }

    @KafkaListener(topics = KafkaTopics.POST_DELETED, groupId = "feed-group")
    public void onPostDeleted(@Payload PostDeletedEvent event) {
        log.info("PostDeleted: {}", event.postId());
        fanoutService.removePost(event.postId(), event.authorId());
    }

    @KafkaListener(topics = KafkaTopics.POST_LIKED, groupId = "feed-group")
    public void onPostLiked(@Payload PostLikedEvent event) {
        log.info("PostLiked: {}", event.postId());
        fanoutService.boostPost(event.postId(), event.postAuthorId());
    }

    @KafkaListener(topics = KafkaTopics.USER_FOLLOWED, groupId = "feed-group")
    public void onUserFollowed(@Payload UserFollowedEvent event) {
        log.info("UserFollowed: {} → {}", event.followerId(), event.followeeId());
        fanoutService.addFolloweePosts(event.followerId(), event.followeeId());
    }

    @KafkaListener(topics = KafkaTopics.USER_UNFOLLOWED, groupId = "feed-group")
    public void onUserUnfollowed(@Payload UserUnfollowedEvent event) {
        log.info("UserUnfollowed: {} → {}", event.followerId(), event.followeeId());
        fanoutService.removeFolloweePosts(event.followerId(), event.followeeId());
    }
}