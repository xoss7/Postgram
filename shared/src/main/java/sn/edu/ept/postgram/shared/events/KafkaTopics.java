package sn.edu.ept.postgram.shared.events;

import lombok.Getter;

@Getter
public enum KafkaTopics {

    USER_REGISTERED("user-registered"),
    POST_PUBLISHED("post-published"),
    POST_LIKED("post-liked"),
    POST_DELETED("post-deleted"),
    COMMENT_ADDED("comment-added"),
    USER_FOLLOWED("user-followed"),
    USER_UNFOLLOWED("user-unfollowed");

    private final String event;

    KafkaTopics(String event) {
        this.event = event;
    }

}