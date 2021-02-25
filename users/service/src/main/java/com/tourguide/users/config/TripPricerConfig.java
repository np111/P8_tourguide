package com.tourguide.users.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tripPricer.TripPricer;

@Configuration
public class TripPricerConfig {
    @Bean
    public TripPricer getTripPricer() {
        return new TripPricer();
    }
}
