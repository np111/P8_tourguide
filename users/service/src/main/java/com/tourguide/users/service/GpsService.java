package com.tourguide.users.service;

import com.tourguide.gps.model.Location;
import com.tourguide.gps.model.NearbyAttraction;
import com.tourguide.gps.model.TrackNearbyAttraction;
import com.tourguide.gps.model.VisitedLocation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * GPS service.
 */
public interface GpsService {
    /**
     * Returns the maximum number of concurrent requests supported by this service before new requests are queued.
     *
     * @return the maximum number of concurrent requests supported by this service
     */
    int getMaxRequests();

    /**
     * Retrieve the user's current location.
     *
     * @param userId User ID
     * @return the user's current location; or empty if unknown
     */
    CompletableFuture<Optional<VisitedLocation>> getUserLocation(UUID userId);

    /**
     * Returns the list of attractions closest to the specified locations.
     * <p>
     * The list is sorted by distance (from nearest to farthest).
     *
     * @param locations   the locations to check (if more than one location is specified, then for each attraction only
     *                    the closest one is considered)
     * @param maxDistance the maximum distance of the results (in miles); infinite if {@code null}
     * @param limit       the maximum number of results; infinite if {@code null}
     * @return the list of nearby attractions
     */
    CompletableFuture<List<NearbyAttraction>> getNearbyAttractions(List<Location> locations, Double maxDistance, Integer limit);

    /**
     * Returns the list of attractions closest to the current user's location AND also the additional specified
     * locations.
     * <p>
     * The list is sorted by distance (from nearest to farthest).
     * <p>
     * This method is a combination of {@link #getUserLocation(UUID)} and
     * {@link #getNearbyAttractions(List, Double, Integer)} (to reduce API calls).
     *
     * @param userId      User ID
     * @param locations   the locations to check (if more than one location is specified, then for each attraction only
     *                    the closest one is considered)
     * @param maxDistance the maximum distance of the results (in miles); infinite if {@code null}
     * @param limit       the maximum number of results; infinite if {@code null}
     * @return the list of nearby attractions
     */
    CompletableFuture<TrackNearbyAttraction> trackNearbyAttractions(UUID userId, List<Location> locations, Double maxDistance, Integer limit);
}
