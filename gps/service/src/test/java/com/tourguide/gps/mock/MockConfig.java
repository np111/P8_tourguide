package com.tourguide.gps.mock;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

@TestConfiguration
public class MockConfig {
    @Bean
    public TaskScheduler taskScheduler() {
        // Returns a disabled TaskScheduler
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
