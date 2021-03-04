package com.tourguide.users.service;

import com.tourguide.gps.model.Attraction;
import com.tourguide.gps.model.Location;
import com.tourguide.users.mock.MockConfig;
import com.tourguide.users.model.User;
import com.tourguide.users.service.impl.InternalUserService;
import com.tourguide.util.ConcurrentThrottler;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import(MockConfig.class)
class TrackingServiceTest { // (legacy name: TestPerformance)
    @Autowired
    private InternalUserService userService;
    @Autowired
    private TrackingService trackingService;
    @Autowired
    private RewardsService rewardsService;

    @Test
    void trackUsers() throws InterruptedException { // (legacy name: highVolumeTrackLocation)
        userService.setUsersNumber(100);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        trackingService.trackUsers();
        stopWatch.stop();

        System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds."); // (legacy logs)
        assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }

    @Test
    public void registerRewards() throws InterruptedException { // (legacy name: highVolumeGetRewards)
        userService.setUsersNumber(100);

        Attraction attraction = Attraction.builder()
                .name("Attraction")
                .city("City")
                .state("State")
                .location(Location.builder().build())
                .build();

        ConcurrentThrottler<User, Void> registerRewards = ConcurrentThrottler.<User, Void>builder()
                .limit(rewardsService.getMaxRequests())
                .function(user -> trackingService.registerRewards(user, attraction))
                .build();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<User> allUsers = userService.getAllUsers();
        CountDownLatch cdl = new CountDownLatch(allUsers.size());
        allUsers.forEach(user -> registerRewards.call(user).whenComplete((ignored1, ignored2) -> cdl.countDown()));
        cdl.await();
        stopWatch.stop();

        for (User user : userService.getAllUsers()) {
            assertFalse(user.getRewards().isEmpty());
        }

        System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds."); // (legacy logs)
        assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }
}
