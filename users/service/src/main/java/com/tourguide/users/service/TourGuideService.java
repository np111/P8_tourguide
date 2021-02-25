package com.tourguide.users.service;

import com.tourguide.gps.model.Location;
import com.tourguide.gps.model.VisitedLocation;
import com.tourguide.users.model.TripDeal;
import com.tourguide.users.model.User;
import com.tourguide.users.model.UserNearbyAttractions;
import com.tourguide.users.model.UserReward;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class TourGuideService {
    private final UserService userService;
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
        // TODO
        return null;
    }

    public List<TripDeal> getTripDeals(String userName) {
        User user = userService.getUser(userName).orElse(null);
        return user == null ? Collections.emptyList() : tripPricerService.getTripDeals(user);
    }
}
