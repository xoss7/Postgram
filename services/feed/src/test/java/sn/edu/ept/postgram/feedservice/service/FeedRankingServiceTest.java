package sn.edu.ept.postgram.feedservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sn.edu.ept.postgram.feedservice.config.FeedProperties;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FeedRankingServiceTest {

    private FeedRankingService feedRankingService;

    @BeforeEach
    void setUp() {
        FeedProperties properties = new FeedProperties();
        properties.getRanking().setFreshnessWeight(1000.0);
        properties.getRanking().setLikeWeight(4.0);
        properties.getRanking().setCommentWeight(6.0);
        properties.getRanking().setRecencyHalfLifeHours(24.0);
        feedRankingService = new FeedRankingService(properties);
    }

    @Test
    void shouldRankNewerPostHigherWhenEngagementIsEqual() {
        double freshScore = feedRankingService.computeScore(Instant.now().minus(1, ChronoUnit.HOURS), 10, 5);
        double oldScore = feedRankingService.computeScore(Instant.now().minus(48, ChronoUnit.HOURS), 10, 5);

        assertTrue(freshScore > oldScore);
    }

    @Test
    void shouldRankMoreEngagingPostHigherWhenRecencyIsEqual() {
        Instant createdAt = Instant.now().minus(2, ChronoUnit.HOURS);

        double weakScore = feedRankingService.computeScore(createdAt, 1, 0);
        double strongScore = feedRankingService.computeScore(createdAt, 50, 10);

        assertTrue(strongScore > weakScore);
    }
}
