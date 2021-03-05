package com.tourguide.gps;

import com.google.common.base.Preconditions;
import com.tourguide.gps.model.Attraction;
import com.tourguide.gps.model.Location;
import com.tourguide.gps.model.NearbyAttraction;
import com.tourguide.gps.model.VisitedLocation;
import gpsUtil.GpsUtil;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
@Slf4j
public class GpsService implements InitializingBean {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945D;
    private static final long UPDATE_ATTRACTIONS_RATE = 15_000; // ms

    private final GpsUtil gpsUtil;
    private final GpsUtilMapper gpsUtilMapper;
    private List<Attraction> attractions = Collections.emptyList();

    @Override
    public void afterPropertiesSet() {
        updateAttractions();
    }

    @Scheduled(initialDelay = UPDATE_ATTRACTIONS_RATE, fixedRate = UPDATE_ATTRACTIONS_RATE)
    private void updateAttractions() {
        log.debug("Update cached attractions list");
        attractions = Collections.unmodifiableList(gpsUtil.getAttractions().stream()
                .map(gpsUtilMapper::toAttraction)
                .collect(Collectors.toList()));
    }

    public List<Attraction> getAttractions() {
        return attractions;
    }

    public Optional<VisitedLocation> getUserLocation(UUID userId) {
        return Optional.of(gpsUtilMapper.toVisitedLocation(gpsUtil.getUserLocation(userId)));
    }

    public List<NearbyAttraction> getNearbyAttractions(List<Location> locations, Double maxDistance, Integer limit) {
        Preconditions.checkArgument(!locations.isEmpty(), "locations cannot be empty!");
        Stream<NearbyAttraction> ret = attractions.stream().map(attraction -> {
            Location bestLocation = null;
            double bestDistance = Double.MAX_VALUE;
            for (Location location : locations) {
                double distance = getDistanceInMile(location, attraction.getLocation());
                if (bestLocation == null || distance < bestDistance) {
                    bestLocation = location;
                    bestDistance = distance;
                }
            }
            return NearbyAttraction.builder()
                    .attraction(attraction)
                    .location(bestLocation)
                    .distance(bestDistance)
                    .build();
        });
        if (maxDistance != null) {
            ret = ret.filter(nearbyAttraction -> nearbyAttraction.getDistance() < maxDistance);
        }
        ret = ret.sorted(Comparator.comparing(NearbyAttraction::getDistance));
        if (limit != null) {
            ret = ret.limit(limit);
        }
        return ret.collect(Collectors.toList());
    }

    private double getDistanceInMile(Location a, Location b) {
        double lat1 = Math.toRadians(a.getLatitude());
        double lon1 = Math.toRadians(a.getLongitude());
        double lat2 = Math.toRadians(b.getLatitude());
        double lon2 = Math.toRadians(b.getLongitude());

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        return STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
    }
}
