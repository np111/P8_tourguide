package com.tourguide.users.service;

import com.tourguide.gps.model.Location;
import com.tourguide.gps.model.NearbyAttraction;
import com.tourguide.gps.model.TrackNearbyAttraction;
import com.tourguide.gps.model.VisitedLocation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface GpsService {
    int getMaxRequests();

    CompletableFuture<Optional<VisitedLocation>> getUserLocation(UUID userId);

    CompletableFuture<List<NearbyAttraction>> getNearbyAttractions(List<Location> locations, Double maxDistance, Integer limit);

    CompletableFuture<TrackNearbyAttraction> trackNearbyAttractions(UUID userId, List<Location> locations, Double maxDistance, Integer limit);
}
