package com.tourguide.users.mock;

import com.tourguide.users.service.GpsService;
import com.tourguide.users.service.RewardsService;
import com.tourguide.users.service.TrackingService;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

@TestConfiguration
public class MockConfig {
    @Bean
    @Primary
    public GpsService getGpsService() {
        return new MockGpsService();
    }

    @Bean
    @Primary
    public RewardsService getRewardsService() {
        return new MockRewardsService();
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new TaskScheduler() {
            @Override
            public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
                return null;
            }

            @Override
            public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
                return null;
            }

            @Override
            public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
                return null;
            }

            @Override
            public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
                return null;
            }

            @Override
            public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
                return null;
            }

            @Override
            public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
                return null;
            }
        };
    }
}
