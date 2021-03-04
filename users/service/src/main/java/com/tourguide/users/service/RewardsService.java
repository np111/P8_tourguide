package com.tourguide.users.service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface RewardsService {
    int getMaxRequests();

    CompletableFuture<Integer> getAttractionRewardPoints(UUID attractionId, UUID userId);
}
