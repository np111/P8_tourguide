package com.tourguide.users.service;

import com.tourguide.gps.model.Attraction;
import com.tourguide.gps.model.Location;
import com.tourguide.gps.model.NearbyAttraction;
import com.tourguide.gps.model.VisitedLocation;
import com.tourguide.users.model.User;
import com.tourguide.users.model.UserReward;
import com.tourguide.users.properties.TrackingProperties;
import com.tourguide.users.util.UserNotFoundException;
import com.tourguide.util.ConcurrentThrottler;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.tourguide.users.util.LogUtil.formatMillis;

/**
 * Position tracking service.
 */
@Service
@Slf4j
public class TrackingService implements InitializingBean {
    private static final long SECOND_NANO = TimeUnit.SECONDS.toNanos(1);

    /**
     * Interval (in milliseconds) to update user tracking.
     */
    private static final long TRACK_USERS_RATE = 5 * 60 * 1000L;

    /**
     * Number of concurrent queries to leave free for {@linkplain GpsService#getMaxRequests() GpsService} and
     * {@linkplain RewardsService#getMaxRequests() RewardsService}.
     */
    private static final int SERVICES_PRESSURE_FREE_REQUESTS = 48;

    private final UserService userService;
    private final GpsService gpsService;
    private final RewardsService rewardsService;

    /**
     * Distance (in miles) at which a user is considered to have visited an attraction.
     */
    private @Setter double proximityBuffer;

    /**
     * Should the JVM be warmed up to get the best performance immediately (for benchmarks).
     */
    private final boolean warmup;

    @Autowired
    public TrackingService(TrackingProperties props, UserService userService, GpsService gpsService, RewardsService rewardsService) {
        this.userService = userService;
        this.gpsService = gpsService;
        this.rewardsService = rewardsService;
        this.proximityBuffer = props.getProximityBuffer();
        this.warmup = props.isWarmup();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (warmup) {
            doWarmup();
        }
    }

    /**
     * Scheduled methods, which track users' location changes; to save their history and offer them their rewards.
     */
    @Scheduled(initialDelay = 5_000L, fixedRate = TRACK_USERS_RATE)
    public void trackUsers() throws InterruptedException {
        // TODO: abort after too many failures/exceptions

        // Init the concurrent methods' optimized callers.
        ConcurrentThrottler<User, List<RewardEntry>> trackUserLocation = ConcurrentThrottler.<User, List<RewardEntry>>builder()
                .limit(Math.max(1, gpsService.getMaxRequests() - SERVICES_PRESSURE_FREE_REQUESTS))
                .function(this::trackUserLocation)
                .build();

        ConcurrentThrottler<RewardEntry, Void> registerRewards = ConcurrentThrottler.<RewardEntry, Void>builder()
                .limit(Math.max(1, rewardsService.getMaxRequests() - SERVICES_PRESSURE_FREE_REQUESTS))
                .function(this::registerRewards)
                .build();

        // Then retrieve all users (and start logging)
        List<User> users = userService.getAllUsers();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("Begin Tracker. Tracking {} users.", users.size());

        // Start the concurrent calls pipeline
        CountDownLatch cdl = new CountDownLatch(users.size());
        users.forEach(user -> trackUserLocation.call(user)
                .thenCompose(registerRewards::callAll)
                .whenComplete((ignored, ex) -> {
                    cdl.countDown();

                    if (ex != null) {
                        log.warn("Exception tracking user:", ex);
                    }
                }));

        // Finally wait for the end of the operation (and log every seconds)
        int prevCount = 0;
        while (true) {
            boolean done = cdl.await(SECOND_NANO - (stopWatch.getNanoTime() % SECOND_NANO), TimeUnit.NANOSECONDS);
            long time = stopWatch.getTime();

            int count = users.size() - (int) cdl.getCount();
            int deltaCount = count - prevCount;
            prevCount = count;

            String totalStr = Integer.toString(users.size());
            String countStr = StringUtils.leftPad(Integer.toString(count), totalStr.length());
            String deltaCountStr = StringUtils.leftPad(Integer.toString(deltaCount), totalStr.length());
            log.debug("Tracking progression: {}/{} Δ{} | {}", countStr, totalStr, deltaCountStr, formatMillis(time));
            if (log.isTraceEnabled()) {
                log.trace("  trackUserLocation - " + trackUserLocation.logString());
                log.trace("    registerRewards - " + registerRewards.logString());
            }

            if (done) {
                log.info("Tracker Time Elapsed: {}.", formatMillis(time));
                break;
            }
        }
    }

    private CompletableFuture<List<RewardEntry>> trackUserLocation(User user) {
        List<Location> locations = user.getVisitedLocations().stream().map(VisitedLocation::getLocation).collect(Collectors.toList());
        return gpsService.trackNearbyAttractions(user.getId(), locations, proximityBuffer, null).thenApplyAsync(r -> {
            VisitedLocation visitedLocation = VisitedLocation.builder()
                    .location(r.getUserLocation().getLocation())
                    .timeVisited(r.getUserLocation().getTimeVisited())
                    .build();

            // Register new visited location
            user.getVisitedLocations().add(visitedLocation);
            try {
                userService.registerVisitedLocation(user.getName(), visitedLocation);
            } catch (UserNotFoundException ignored) {
            }

            // Returns newly visited attractions for rewarding
            List<RewardEntry> ret = new ArrayList<>();
            for (NearbyAttraction rewardAttraction : r.getNearbyAttractions()) {
                if (shouldProcessReward(user.getName(), rewardAttraction.getAttraction().getName())) {
                    VisitedLocation location = user.getVisitedLocations().stream()
                            .filter(x -> x.getLocation().equals(rewardAttraction.getLocation()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("matching visited location is missing"));
                    ret.add(new RewardEntry(user.getId(), user.getName(), location, rewardAttraction.getAttraction()));
                }
            }
            return ret;
        });
    }

    private boolean shouldProcessReward(String userName, String attractionName) {
        try {
            return !userService.hasRewardForAttraction(userName, attractionName);
        } catch (UserNotFoundException ignored) {
            return false;
        }
    }

    private CompletableFuture<Void> registerRewards(RewardEntry e) {
        return rewardsService
                .getAttractionRewardPoints(e.getAttraction().getId(), e.getUserId())
                .thenApply(rewardPoints -> UserReward.builder()
                        .visitedLocation(e.getVisitedLocation())
                        .attraction(e.getAttraction())
                        .rewardPoints(rewardPoints)
                        .build())
                .thenAcceptAsync(reward -> {
                    try {
                        userService.registerReward(e.getUserName(), reward);
                    } catch (UserNotFoundException ignored) {
                    }
                });
    }

    CompletableFuture<Void> registerRewards(User user, Attraction attraction) {
        return CompletableFuture.allOf(user.getVisitedLocations().stream()
                .map(location -> registerRewards(new RewardEntry(user.getId(), user.getName(), location, attraction)))
                .toArray(CompletableFuture[]::new));
    }

    /**
     * Magic method to warm up the JVM (mainly because of JIT) in order to get the best performance immediately during
     * benchmarks. This method is optimized for HotSpot JVM.
     */
    private void doWarmup() throws InterruptedException {
        log.debug("Warmup TrackingService...");

        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            throw new IllegalStateException("No users");
        }

        int concurrencyLimit = Math.min(users.size() * 10, 12_000);
        int totalWarmupCount = Math.max(concurrencyLimit * 10, 12_000);
        Semaphore semaphore = new Semaphore(concurrencyLimit);
        AtomicInteger warmupCount = new AtomicInteger();
        for (int i = 0; ; ++i) {
            semaphore.acquire();
            if (warmupCount.get() >= totalWarmupCount) {
                semaphore.release();
                break;
            }

            User user = userService.getUser(users.get(i % users.size()).getName());
            CompletableFuture<?> future = trackUserLocation(user).whenComplete((ignored, ex) -> {
                if (ex == null) {
                    warmupCount.incrementAndGet();
                }
                semaphore.release();
            });

            if (warmupCount.get() == 0) {
                try {
                    future.join();
                } catch (Throwable ignored) {
                }
            }
        }

        semaphore.acquire(concurrencyLimit);
        Thread.sleep(2000L);

        log.debug("TrackingService is warmed up!");
    }

    @Data
    private static class RewardEntry {
        private final UUID userId;
        private final String userName;
        private final VisitedLocation visitedLocation;
        private final Attraction attraction;
    }
}
