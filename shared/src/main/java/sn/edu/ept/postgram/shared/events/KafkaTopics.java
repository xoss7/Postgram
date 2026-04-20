package sn.edu.ept.postgram.shared.events;

public final class KafkaTopics {

    private KafkaTopics() {}

    public static final String USER_REGISTERED = "user-registered";
    public static final String POST_PUBLISHED = "post-published";
    public static final String POST_LIKED = "post-liked";
    public static final String POST_UNLIKED = "post-unliked";
    public static final String POST_DELETED = "post-deleted";
    public static final String COMMENT_ADDED = "comment-added";
    public static final String MESSAGE_SENT = "message-sent";
    public static final String USER_FOLLOWED = "user-followed";
    public static final String USER_UNFOLLOWED = "user-unfollowed";
}