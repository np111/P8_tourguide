package com.tourguide.users.mock;

import com.tourguide.users.service.RewardsService;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.Getter;

/**
 * A mock for GpsService which re-implement the RewardCentral.jar logic (with sleeps).
 */
public class MockRewardsService implements RewardsService {
    private final @Getter int maxRequests = 1000;
    private final ThreadPoolExecutor executor;

    public MockRewardsService() {
        executor = new ThreadPoolExecutor(maxRequests, maxRequests, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        executor.allowCoreThreadTimeOut(true);
    }

    @Override
    public CompletableFuture<Integer> getAttractionRewardPoints(UUID attractionId, UUID userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(1, 1000));
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            return ThreadLocalRandom.current().nextInt(1, 1000);
        }, executor);
    }
}
