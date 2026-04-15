package sn.edu.ept.postgram.feedservice.service;

import org.springframework.stereotype.Service;
import sn.edu.ept.postgram.feedservice.config.FeedProperties;
import sn.edu.ept.postgram.feedservice.model.PostEntity;

import java.time.Duration;
import java.time.Instant;

@Service
public class FeedRankingService {

    private final FeedProperties properties;

    public FeedRankingService(FeedProperties properties) {
        this.properties = properties;
    }

    public double computeScore(PostEntity post) {
        return computeScore(post.getCreatedAt(), post.getLikesCount(), post.getCommentsCount());
    }

    public double computeScore(Instant createdAt, long likesCount, long commentsCount) {
        Instant safeCreatedAt = createdAt != null ? createdAt : Instant.now();
        double ageHours = Math.max(0d, Duration.between(safeCreatedAt, Instant.now()).toMinutes() / 60.0d);
        double halfLife = Math.max(1d, properties.getRanking().getRecencyHalfLifeHours());
        double freshness = properties.getRanking().getFreshnessWeight() / (1d + (ageHours / halfLife));

        return freshness
                + likesCount * properties.getRanking().getLikeWeight()
                + commentsCount * properties.getRanking().getCommentWeight();
    }
}
