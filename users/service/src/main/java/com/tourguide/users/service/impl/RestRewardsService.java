package com.tourguide.users.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourguide.restclient.JacksonRestSerializer;
import com.tourguide.restclient.RestCall;
import com.tourguide.restclient.RestClient;
import com.tourguide.users.properties.RewardsServiceProperties;
import com.tourguide.users.service.RewardsService;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestRewardsService implements RewardsService {
    private final @Getter int maxRequests;
    private final RestClient restClient;

    @Autowired
    public RestRewardsService(RewardsServiceProperties props, ObjectMapper objectMapper) {
        this.maxRequests = props.getMaxConcurrentRequests();
        this.restClient = RestClient.builder()
                .baseUrl(props.getUrl())
                .serializer(new JacksonRestSerializer(objectMapper))
                .maxRequests(maxRequests)
                .maxIdleConnections(Math.max(1, maxRequests / 4))
                .keepAliveDuration(Duration.ofSeconds(15))
                .callTimeout(Duration.ofSeconds(90))
                .build();
    }

    public CompletableFuture<Integer> getAttractionRewardPoints(UUID attractionId, UUID userId) {
        return restClient.call(RestCall
                .of(Integer.class)
                .path("getAttractionRewardPoints")
                .query("attractionId", attractionId.toString())
                .query("userId", userId.toString()));
    }
}
