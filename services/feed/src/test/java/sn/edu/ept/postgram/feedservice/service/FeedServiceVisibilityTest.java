package sn.edu.ept.postgram.feedservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import sn.edu.ept.postgram.feedservice.config.FeedProperties;
import sn.edu.ept.postgram.feedservice.model.PostEntity;
import sn.edu.ept.postgram.feedservice.model.PostVisibility;
import sn.edu.ept.postgram.feedservice.repository.FollowRelationRepository;
import sn.edu.ept.postgram.feedservice.repository.PostRepository;
import sn.edu.ept.postgram.feedservice.repository.UserFeedRepository;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FeedServiceVisibilityTest {

    private FeedService feedService;
    private FollowRelationRepository followRelationRepository;

    @BeforeEach
    void setUp() {
        PostRepository postRepository = mock(PostRepository.class);
        followRelationRepository = mock(FollowRelationRepository.class);
        UserFeedRepository userFeedRepository = mock(UserFeedRepository.class);
        FeedCacheService cacheService = mock(FeedCacheService.class);
        FeedRankingService rankingService = new FeedRankingService(new FeedProperties());
        feedService = new FeedService(
                postRepository,
                followRelationRepository,
                userFeedRepository,
                cacheService,
                rankingService,
                new FeedProperties()
        );
    }

    @Test
    void shouldHidePrivatePostFromNonAuthor() {
        UUID authorId = UUID.randomUUID();
        UUID viewerId = UUID.randomUUID();
        PostEntity post = buildPost(authorId, PostVisibility.PRIVATE);

        assertFalse(feedService.isVisibleTo(post, viewerId));
    }

    @Test
    void shouldShowFollowersOnlyPostToFollowers() {
        UUID authorId = UUID.randomUUID();
        UUID viewerId = UUID.randomUUID();
        PostEntity post = buildPost(authorId, PostVisibility.FOLLOWERS_ONLY);
        when(followRelationRepository.existsByFollowerIdAndFolloweeId(viewerId, authorId)).thenReturn(true);

        assertTrue(feedService.isVisibleTo(post, viewerId));
    }

    @Test
    void shouldHideFollowersOnlyPostFromNonFollowers() {
        UUID authorId = UUID.randomUUID();
        UUID viewerId = UUID.randomUUID();
        PostEntity post = buildPost(authorId, PostVisibility.FOLLOWERS_ONLY);
        when(followRelationRepository.existsByFollowerIdAndFolloweeId(viewerId, authorId)).thenReturn(false);

        assertFalse(feedService.isVisibleTo(post, viewerId));
    }

    private PostEntity buildPost(UUID authorId, PostVisibility visibility) {
        PostEntity post = new PostEntity();
        ReflectionTestUtils.setField(post, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(post, "authorId", authorId);
        ReflectionTestUtils.setField(post, "caption", "caption");
        ReflectionTestUtils.setField(post, "mediaUrl", "media");
        ReflectionTestUtils.setField(post, "visibility", visibility);
        ReflectionTestUtils.setField(post, "likesCount", 10L);
        ReflectionTestUtils.setField(post, "commentsCount", 2L);
        ReflectionTestUtils.setField(post, "createdAt", Instant.now());
        ReflectionTestUtils.setField(post, "deleted", false);
        return post;
    }
}
