package com.tourguide.users.service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Rewards service.
 */
public interface RewardsService {
    /**
     * Returns the maximum number of concurrent requests supported by this service before new requests are queued.
     *
     * @return the maximum number of concurrent requests supported by this service
     */
    int getMaxRequests();

    /**
     * Returns the number of points the user can earn if he visit the attraction.
     *
     * @param attractionId ID of the visited attraction
     * @param userId       User ID
     * @return the number of points; or {@code null} if unknown
     */
    CompletableFuture<Integer> getAttractionRewardPoints(UUID attractionId, UUID userId);
}
