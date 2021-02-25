package com.tourguide.users.service;

import com.google.common.collect.Iterables;
import com.tourguide.gps.model.Location;
import com.tourguide.gps.model.VisitedLocation;
import com.tourguide.users.model.TripDeal;
import com.tourguide.users.model.User;
import com.tourguide.users.model.UserNearbyAttraction;
import com.tourguide.users.model.UserNearbyAttractions;
import com.tourguide.users.model.UserReward;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class TourGuideService {
    private final UserService userService;
    private final GpsService gpsService;
    private final RewardsService rewardsService;
    private final TripPricerService tripPricerService;

    public VisitedLocation getUserLocation(String userName) {
        return userService.getUserLocation(userName).orElse(null);
    }

    public List<UserReward> getUserRewards(String userName) {
        return userService.getUserRewards(userName);
    }

    public Map<UUID, Location> getAllCurrentLocations() {
        return userService.getAllCurrentLocations();
    }

    public UserNearbyAttractions getNearbyAttractions(String userName) {
        User user = userService.getUser(userName).orElse(null);
        VisitedLocation location = user == null ? null : Iterables.getLast(user.getVisitedLocations(), null);
        if (location == null) {
            return UserNearbyAttractions.builder().nearbyAttractions(Collections.emptyList()).build();
        }

        // Fetch nearby attractions
        List<UserNearbyAttraction> nearbyAttractions = gpsService
                .getNearbyAttractions(Collections.singletonList(location.getLocation()), null, 5)
                .join().stream()
                .map(e -> UserNearbyAttraction.builder()
                        .attraction(e.getAttraction())
                        .distance(e.getDistance())
                        .build())
                .collect(Collectors.toList());

        // Set reward points
        CompletableFuture.allOf(nearbyAttractions.stream()
                .map(e -> rewardsService
                        .getAttractionRewardPoints(e.getAttraction().getId(), user.getId())
                        .thenAccept(e::setRewardPoints))
                .toArray(CompletableFuture[]::new))
                .join();

        // Build response
        return UserNearbyAttractions.builder()
                .userLocation(location.getLocation())
                .nearbyAttractions(nearbyAttractions)
                .build();
    }

    public List<TripDeal> getTripDeals(String userName) {
        User user = userService.getUser(userName).orElse(null);
        return user == null ? Collections.emptyList() : tripPricerService.getTripDeals(user);
    }
}
