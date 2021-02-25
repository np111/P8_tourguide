package com.tourguide.users.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourguide.gps.model.Location;
import com.tourguide.gps.model.NearbyAttraction;
import com.tourguide.gps.model.TrackNearbyAttraction;
import com.tourguide.gps.model.VisitedLocation;
import com.tourguide.restclient.JacksonRestSerializer;
import com.tourguide.restclient.RestCall;
import com.tourguide.restclient.RestClient;
import com.tourguide.users.properties.GpsServiceProperties;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GpsService {
    private final @Getter int maxRequests;
    private final RestClient restClient;

    @Autowired
    public GpsService(GpsServiceProperties props, ObjectMapper objectMapper) {
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

    public CompletableFuture<Optional<VisitedLocation>> getUserLocation(UUID userId) {
        return restClient.call(RestCall
                .of(VisitedLocation.class)
                .path("userLocation")
                .query("userId", userId.toString()))
                .thenApply(Optional::ofNullable);
    }

    public CompletableFuture<List<NearbyAttraction>> getNearbyAttractions(List<Location> locations, Double maxDistance, Integer limit) {
        return restClient.call(RestCall
                .ofList(NearbyAttraction.class)
                .path("nearbyAttractions")
                .apply(c -> nearbyAttractionsParameters(c, locations, maxDistance, limit)));
    }

    public CompletableFuture<TrackNearbyAttraction> trackNearbyAttractions(UUID userId, List<Location> locations, Double maxDistance, Integer limit) {
        return restClient.call(RestCall
                .of(TrackNearbyAttraction.class)
                .path("trackNearbyAttractions")
                .query("userId", userId.toString())
                .apply(c -> nearbyAttractionsParameters(c, locations, maxDistance, limit)));
    }

    private void nearbyAttractionsParameters(RestCall<?> call, List<Location> locations, Double maxDistance, Integer limit) {
        locations.forEach(location -> call.query("location", location.getLatitude() + "," + location.getLongitude()));
        if (maxDistance != null) {
            call.query("maxDistance", Double.toString(maxDistance));
        }
        if (limit != null) {
            call.query("limit", Integer.toString(limit));
        }
    }
}
