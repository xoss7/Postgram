package sn.edu.ept.postgram.feedservice.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.edu.ept.postgram.feedservice.config.FeedProperties;
import sn.edu.ept.postgram.feedservice.dto.api.FeedItemResponse;
import sn.edu.ept.postgram.feedservice.dto.api.FeedResponse;
import sn.edu.ept.postgram.feedservice.dto.event.PostDeletedEvent;
import sn.edu.ept.postgram.feedservice.dto.event.PostInteractionEvent;
import sn.edu.ept.postgram.feedservice.dto.event.PostPublishedEvent;
import sn.edu.ept.postgram.feedservice.dto.event.UserFollowedEvent;
import sn.edu.ept.postgram.feedservice.dto.event.UserUnfollowedEvent;
import sn.edu.ept.postgram.feedservice.model.FollowRelationEntity;
import sn.edu.ept.postgram.feedservice.model.PostEntity;
import sn.edu.ept.postgram.feedservice.model.PostVisibility;
import sn.edu.ept.postgram.feedservice.model.UserEntity;
import sn.edu.ept.postgram.feedservice.repository.FollowRelationRepository;
import sn.edu.ept.postgram.feedservice.repository.PostRepository;
import sn.edu.ept.postgram.feedservice.repository.UserFeedRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FeedService {

    private final PostRepository postRepository;
    private final FollowRelationRepository followRelationRepository;
    private final UserFeedRepository userRepository;
    private final FeedCacheService feedCacheService;
    private final FeedRankingService feedRankingService;
    private final FeedProperties properties;

    public FeedService(
            PostRepository postRepository,
            FollowRelationRepository followRelationRepository,
            UserFeedRepository userRepository,
            FeedCacheService feedCacheService,
            FeedRankingService feedRankingService,
            FeedProperties properties
    ) {
        this.postRepository = postRepository;
        this.followRelationRepository = followRelationRepository;
        this.userRepository = userRepository;
        this.feedCacheService = feedCacheService;
        this.feedRankingService = feedRankingService;
        this.properties = properties;
    }

    @Transactional(readOnly = true)
    public FeedResponse getFeed(UUID userId, int offset, int requestedLimit) {
        int limit = normalizeLimit(requestedLimit);
        int safeOffset = Math.max(offset, 0);

        if (!feedCacheService.hasFeed(userId)) {
            rebuildFeed(userId);
        }

        Set<UUID> cachedIds = feedCacheService.getFeedPostIds(userId, safeOffset, limit);
        if (cachedIds.isEmpty() && safeOffset == 0) {
            rebuildFeed(userId);
            cachedIds = feedCacheService.getFeedPostIds(userId, safeOffset, limit);
        }

        List<PostEntity> posts = fetchOrderedPosts(cachedIds);

        // Hydratation des auteurs
        Set<UUID> authorIds = posts.stream().map(PostEntity::getAuthorId).collect(Collectors.toSet());
        Map<UUID, UserEntity> authors = userRepository.findAllById(authorIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, user -> user));

        List<FeedItemResponse> items = posts.stream()
                .filter(post -> isVisibleTo(post, userId))
                .map(post -> toResponse(post, authors.get(post.getAuthorId())))
                .toList();

        long total = feedCacheService.size(userId);
        boolean hasMore = safeOffset + items.size() < total;

        return new FeedResponse(items, safeOffset, limit, items.size(), hasMore);
    }

    @Transactional
    public void handlePostPublished(PostPublishedEvent event) {
        if (event == null || event.postId() == null || event.authorId() == null) {
            return;
        }

        // Sauvegarder le post dans la DB locale du service Feed si pas déjà présent
        if (!postRepository.existsById(event.postId())) {
            PostEntity post = new PostEntity();
            post.setId(event.postId());
            post.setAuthorId(event.authorId());
            post.setCreatedAt(event.createdAt());
            post.setVisibility(PostVisibility.valueOf(event.visibility()));
            post.setLikesCount(event.likesCount());
            post.setCommentsCount(event.commentsCount());
            post.setCaption(event.caption());
            post.setMediaUrl(event.mediaUrl());
            postRepository.save(post);
        }

        // 1. Récupérer les followers de l'auteur depuis Postgres
        Set<UUID> recipientIds = followRelationRepository.findByFolloweeId(event.authorId())
                .stream()
                .map(FollowRelationEntity::getFollowerId)
                .collect(Collectors.toSet());

        // Ajouter l'auteur lui-même à son propre feed
        recipientIds.add(event.authorId());

        // 2. Score initial
        double score = feedRankingService.computeScore(event.createdAt(), event.likesCount(), event.commentsCount());

        // 3. Stocker dans Redis via Pipeline
        feedCacheService.addPostToMultipleFeeds(recipientIds, event.postId(), score);

        System.out.println("Stored post " + event.postId() + " in " + recipientIds.size() + " feeds.");
    }

    @Transactional
    public void handlePostInteraction(PostInteractionEvent event) {
        if (event == null || event.postId() == null) {
            return;
        }

        Optional<PostEntity> postOptional = postRepository.findByIdAndDeletedFalse(event.postId());
        if (postOptional.isEmpty()) {
            feedCacheService.removePostEverywhere(event.postId());
            return;
        }

        PostEntity post = postOptional.get();

        // Mettre à jour les compteurs en DB
        post.setLikesCount(event.likesCount());
        post.setCommentsCount(event.commentsCount());
        postRepository.save(post);

        // Recalculer le score complet
        double newScore = feedRankingService.computeScore(post);

        Set<UUID> owners = feedCacheService.getFeedOwners(post.getId());
        for (UUID ownerId : owners) {
            if (isVisibleTo(post, ownerId)) {
                // On met à jour le score dans Redis (écraser l'ancien score par le nouveau score calculé)
                feedCacheService.updateScore(ownerId, post.getId(), newScore);
            } else {
                feedCacheService.removePostFromUserFeed(ownerId, post.getId());
            }
        }
    }

    @Transactional
    public void handleUserUnfollowed(UserUnfollowedEvent event) {
        if (event == null || event.followerId() == null || event.followeeId() == null) {
            return;
        }

        // Supprimer la relation de Postgres
        followRelationRepository.deleteById(new FollowRelationEntity.FollowRelationId(event.followerId(), event.followeeId()));

        // Récupérer tous les IDs de posts de l'auteur désabonné
        Set<UUID> postIds = postRepository.findByAuthorIdAndDeletedFalse(event.followeeId())
                .stream()
                .map(PostEntity::getId)
                .collect(Collectors.toSet());

        if (!postIds.isEmpty()) {
            feedCacheService.removePostsFromUserFeed(event.followerId(), postIds);
        }
    }

    @Transactional
    public void handleUserFollowed(UserFollowedEvent event) {
        if (event == null || event.followerId() == null || event.followeeId() == null) {
            return;
        }

        // Sauvegarder la relation dans Postgres pour les futurs événements de publication
        if (!followRelationRepository.existsByFollowerIdAndFolloweeId(event.followerId(), event.followeeId())) {
            FollowRelationEntity relation = new FollowRelationEntity(event.followerId(), event.followeeId());
            followRelationRepository.save(relation);
        }

        List<PostEntity> recentPosts = postRepository.findRecentVisiblePostsByAuthor(
                event.followeeId(),
                PostVisibility.PRIVATE,
                PageRequest.of(0, properties.getCache().getMaxRecentPostsOnFollow())
        );

        for (PostEntity post : recentPosts) {
            if (isVisibleTo(post, event.followerId())) {
                feedCacheService.addPostToUserFeed(
                        event.followerId(),
                        post.getId(),
                        feedRankingService.computeScore(post)
                );
            }
        }
    }

    @Transactional
    public void handlePostDeleted(PostDeletedEvent event) {
        if (event == null || event.postId() == null) {
            return;
        }
        feedCacheService.removePostEverywhere(event.postId());
    }

    @Transactional(readOnly = true)
    public void rebuildFeed(UUID userId) {
        feedCacheService.clearFeed(userId);

        List<UUID> followeeIds = followRelationRepository.findByFollowerId(userId).stream()
                .map(FollowRelationEntity::getFolloweeId)
                .toList();

        Collection<UUID> authorScope = new ArrayList<>(followeeIds);
        authorScope.add(userId);

        List<PostEntity> candidates = postRepository.findFeedCandidates(
                userId,
                authorScope,
                PageRequest.of(0, properties.getRanking().getMaxInitialAuthorPosts())
        );

        for (PostEntity post : candidates) {
            if (isVisibleTo(post, userId)) {
                feedCacheService.addPostToUserFeed(userId, post.getId(), feedRankingService.computeScore(post));
            }
        }
    }

    private void addToEligibleFeeds(PostEntity post, double score) {
        Set<UUID> recipients = new LinkedHashSet<>();
        recipients.add(post.getAuthorId());

        if (post.getVisibility() == PostVisibility.PUBLIC || post.getVisibility() == PostVisibility.FOLLOWERS_ONLY) {
            followRelationRepository.findByFolloweeId(post.getAuthorId()).stream()
                    .map(FollowRelationEntity::getFollowerId)
                    .forEach(recipients::add);
        }

        for (UUID recipientId : recipients) {
            if (isVisibleTo(post, recipientId)) {
                feedCacheService.addPostToUserFeed(recipientId, post.getId(), score);
            }
        }
    }

    private List<PostEntity> fetchOrderedPosts(Set<UUID> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }

        Map<UUID, Integer> ordering = new LinkedHashMap<>();
        int index = 0;
        for (UUID id : ids) {
            ordering.put(id, index++);
        }

        return postRepository.findByIdInAndDeletedFalse(ids).stream()
                .sorted(Comparator.comparingInt(post -> ordering.getOrDefault(post.getId(), Integer.MAX_VALUE)))
                .toList();
    }

    boolean isVisibleTo(PostEntity post, UUID viewerId) {
        if (post == null || post.isDeleted()) {
            return false;
        }
        if (post.getAuthorId().equals(viewerId)) {
            return true;
        }
        if (post.getVisibility() == PostVisibility.PUBLIC) {
            return true;
        }
        if (post.getVisibility() == PostVisibility.FOLLOWERS_ONLY) {
            return followRelationRepository.existsByFollowerIdAndFolloweeId(viewerId, post.getAuthorId());
        }
        return false;
    }

    private FeedItemResponse toResponse(PostEntity post, UserEntity author) {
        return new FeedItemResponse(
                post.getId(),
                post.getAuthorId(),
                author != null ? author.getUsername() : "Unknown",
                author != null ? author.getProfileUrl() : null,
                post.getCaption(),
                post.getMediaUrl(),
                post.getVisibility().name(),
                post.getLikesCount(),
                post.getCommentsCount(),
                post.getCreatedAt()
        );
    }

    private int normalizeLimit(int requestedLimit) {
        int defaultLimit = properties.getPagination().getDefaultLimit();
        int maxLimit = properties.getPagination().getMaxLimit();

        if (requestedLimit <= 0) {
            return defaultLimit;
        }
        return Math.min(requestedLimit, maxLimit);
    }
}
