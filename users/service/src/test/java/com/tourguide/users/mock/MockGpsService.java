package com.tourguide.users.mock;

import com.google.common.util.concurrent.RateLimiter;
import com.tourguide.gps.model.Attraction;
import com.tourguide.gps.model.Location;
import com.tourguide.gps.model.NearbyAttraction;
import com.tourguide.gps.model.TrackNearbyAttraction;
import com.tourguide.gps.model.VisitedLocation;
import com.tourguide.users.service.GpsService;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * A mock for GpsService which re-implement the gpsUtil.jar logic (with sleeps and rate-limiting).
 */
@SuppressWarnings("UnstableApiUsage")
public class MockGpsService implements GpsService {
    private static final RateLimiter rateLimiter = RateLimiter.create(1000.0D);
    private final @Getter int maxRequests = 1000;
    private final ThreadPoolExecutor executor;

    public MockGpsService() {
        executor = new ThreadPoolExecutor(maxRequests, maxRequests, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        executor.allowCoreThreadTimeOut(true);
    }

    @Override
    public CompletableFuture<Optional<VisitedLocation>> getUserLocation(UUID userId) {
        return CompletableFuture.supplyAsync(() -> Optional.of(getUserLocation0(userId)), executor);
    }

    private VisitedLocation getUserLocation0(UUID userId) {
        rateLimiter.acquire();
        try {
            TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(30, 100));
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        double latitude = Double.parseDouble(String.format("%.6f", ThreadLocalRandom.current().nextDouble(-85.05112878D, 85.05112878D)));
        double longitude = Double.parseDouble(String.format("%.6f", ThreadLocalRandom.current().nextDouble(-180.0D, 180.0D)));
        return VisitedLocation.builder()
                .location(Location.builder()
                        .latitude(latitude)
                        .longitude(longitude)
                        .build())
                .timeVisited(ZonedDateTime.now())
                .build();
    }

    @Override
    public CompletableFuture<List<NearbyAttraction>> getNearbyAttractions(List<Location> locations, Double maxDistance, Integer limit) {
        return CompletableFuture.supplyAsync(() -> {
            return locations.stream().map(location -> NearbyAttraction.builder()
                    .attraction(Attraction.builder()
                            .name("Attraction")
                            .city("Anaheim")
                            .state("CA")
                            .location(location)
                            .build())
                    .distance(0.0D)
                    .location(location)
                    .build())
                    .collect(Collectors.toList());
        }, executor);
    }

    @Override
    public CompletableFuture<TrackNearbyAttraction> trackNearbyAttractions(UUID userId, List<Location> locations, Double maxDistance, Integer limit) {
        return CompletableFuture.supplyAsync(() -> {
            VisitedLocation location = getUserLocation0(userId);

            double c = ThreadLocalRandom.current().nextDouble();
            int nearbyAttractionsCount = (int) (2.2D * c * c * c);
            List<NearbyAttraction> nearbyAttractions = new ArrayList<>(nearbyAttractionsCount);
            for (int i = 0; i < nearbyAttractionsCount; ++i) {
                nearbyAttractions.add(NearbyAttraction.builder()
                        .attraction(Attraction.builder()
                                .name("Attraction" + i)
                                .city("Anaheim")
                                .state("CA")
                                .location(location.getLocation())
                                .build())
                        .distance(0.0D)
                        .location(location.getLocation())
                        .build());
            }

            return TrackNearbyAttraction.builder()
                    .userLocation(location)
                    .nearbyAttractions(nearbyAttractions)
                    .build();
        }, executor);
    }
}
