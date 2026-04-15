package sn.edu.ept.postgram.feedservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "feed")
public class FeedProperties {

    private final Cache cache = new Cache();
    private final Pagination pagination = new Pagination();
    private final Ranking ranking = new Ranking();

    public Cache getCache() {
        return cache;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public Ranking getRanking() {
        return ranking;
    }

    public static class Cache {
        private String prefix = "feed:user:";
        private String postOwnersPrefix = "feed:post:";
        private int maxRecentPostsOnFollow = 25;
        private long ttlHours = 24;

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getPostOwnersPrefix() {
            return postOwnersPrefix;
        }

        public void setPostOwnersPrefix(String postOwnersPrefix) {
            this.postOwnersPrefix = postOwnersPrefix;
        }

        public int getMaxRecentPostsOnFollow() {
            return maxRecentPostsOnFollow;
        }

        public void setMaxRecentPostsOnFollow(int maxRecentPostsOnFollow) {
            this.maxRecentPostsOnFollow = maxRecentPostsOnFollow;
        }

        public long getTtlHours() {
            return ttlHours;
        }

        public void setTtlHours(long ttlHours) {
            this.ttlHours = ttlHours;
        }
    }

    public static class Pagination {
        private int defaultLimit = 20;
        private int maxLimit = 50;

        public int getDefaultLimit() {
            return defaultLimit;
        }

        public void setDefaultLimit(int defaultLimit) {
            this.defaultLimit = defaultLimit;
        }

        public int getMaxLimit() {
            return maxLimit;
        }

        public void setMaxLimit(int maxLimit) {
            this.maxLimit = maxLimit;
        }
    }

    public static class Ranking {
        private double recencyHalfLifeHours = 24;
        private double likeWeight = 4.0;
        private double commentWeight = 6.0;
        private double freshnessWeight = 1000.0;
        private int maxInitialAuthorPosts = 100;

        public double getRecencyHalfLifeHours() {
            return recencyHalfLifeHours;
        }

        public void setRecencyHalfLifeHours(double recencyHalfLifeHours) {
            this.recencyHalfLifeHours = recencyHalfLifeHours;
        }

        public double getLikeWeight() {
            return likeWeight;
        }

        public void setLikeWeight(double likeWeight) {
            this.likeWeight = likeWeight;
        }

        public double getCommentWeight() {
            return commentWeight;
        }

        public void setCommentWeight(double commentWeight) {
            this.commentWeight = commentWeight;
        }

        public double getFreshnessWeight() {
            return freshnessWeight;
        }

        public void setFreshnessWeight(double freshnessWeight) {
            this.freshnessWeight = freshnessWeight;
        }

        public int getMaxInitialAuthorPosts() {
            return maxInitialAuthorPosts;
        }

        public void setMaxInitialAuthorPosts(int maxInitialAuthorPosts) {
            this.maxInitialAuthorPosts = maxInitialAuthorPosts;
        }
    }
}
