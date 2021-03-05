package com.tourguide.users.service;

import com.google.common.collect.Iterables;
import com.tourguide.gps.model.VisitedLocation;
import com.tourguide.users.model.TripDeal;
import com.tourguide.users.model.User;
import com.tourguide.users.model.UserNearbyAttraction;
import com.tourguide.users.model.UserNearbyAttractions;
import com.tourguide.users.util.UserNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    public Optional<UserNearbyAttractions> getNearbyAttractions(String userName) throws UserNotFoundException {
        User user = userService.getUser(userName);
        VisitedLocation location = Iterables.getLast(user.getVisitedLocations(), null);
        if (location == null) {
            return Optional.empty();
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
        return Optional.of(UserNearbyAttractions.builder()
                .userLocation(location.getLocation())
                .nearbyAttractions(nearbyAttractions)
                .build());
    }

    public List<TripDeal> getTripDeals(String userName) throws UserNotFoundException {
        User user = userService.getUser(userName);
        return tripPricerService.getTripDeals(user);
    }
}
