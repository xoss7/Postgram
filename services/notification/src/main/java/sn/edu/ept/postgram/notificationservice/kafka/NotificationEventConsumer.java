package sn.edu.ept.postgram.notificationservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import sn.edu.ept.postgram.notificationservice.model.NotificationType;
import sn.edu.ept.postgram.notificationservice.service.NotificationService;
import sn.edu.ept.postgram.shared.events.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = KafkaTopics.POST_LIKED, groupId = "notification-group")
    public void onPostLiked(@Payload PostLikedEvent event) {
        log.info("Received PostLikedEvent for post: {}", event.postId());
        notificationService.createNotification(
                event.postAuthorId(),
                event.likerId(),
                event.likerUsername(),
                NotificationType.POST_LIKED,
                event.postId()
        );
    }

    @KafkaListener(topics = KafkaTopics.COMMENT_ADDED, groupId = "notification-group")
    public void onCommentAdded(@Payload CommentAddedEvent event) {
        log.info("Received CommentAddedEvent for post: {}", event.postId());
        notificationService.createNotification(
                event.postAuthorId(),
                event.commenterId(),
                event.commenterUsername(),
                NotificationType.COMMENT_ADDED,
                event.postId()
        );
    }

    @KafkaListener(topics = KafkaTopics.USER_FOLLOWED, groupId = "notification-group")
    public void onUserFollowed(@Payload UserFollowedEvent event) {
        log.info("Received UserFollowedEvent: {} followed {}", event.followerUsername(), event.followeeId());
        notificationService.createNotification(
                event.followeeId(),
                event.followerId(),
                event.followerUsername(),
                NotificationType.USER_FOLLOWED,
                null
        );
    }

    @KafkaListener(topics = KafkaTopics.MESSAGE_SENT, groupId = "notification-group")
    public void onMessageReceived(@Payload MessageSentEvent event) {
        log.info("Received MessageSentEvent from: {}", event.senderUsername());
        notificationService.createNotification(
                event.receiverId(),
                event.senderId(),
                event.senderUsername(),
                NotificationType.MESSAGE_RECEIVED,
                event.conversationId()
        );
    }
}
