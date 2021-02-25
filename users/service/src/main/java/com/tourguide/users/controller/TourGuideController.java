package com.tourguide.users.controller;

import com.tourguide.gps.model.Location;
import com.tourguide.gps.model.VisitedLocation;
import com.tourguide.users.model.TripDeal;
import com.tourguide.users.model.UserNearbyAttractions;
import com.tourguide.users.model.UserReward;
import com.tourguide.users.service.TourGuideService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class TourGuideController {
    private final TourGuideService tourGuideService;

    @RequestMapping("/getLocation")
    public VisitedLocation getLocation(@RequestParam String userName) {
        return tourGuideService.getUserLocation(userName);
    }

    @RequestMapping("/getRewards")
    public List<UserReward> getRewards(@RequestParam String userName) {
        return tourGuideService.getUserRewards(userName);
    }

    @RequestMapping("/getAllCurrentLocations")
    public Map<UUID, Location> getAllCurrentLocations() {
        return tourGuideService.getAllCurrentLocations();
    }

    @RequestMapping("/getNearbyAttractions")
    public UserNearbyAttractions getNearbyAttractions(@RequestParam String userName) {
        return tourGuideService.getNearbyAttractions(userName);
    }

    @RequestMapping("/getTripDeals")
    public List<TripDeal> getTripDeals(@RequestParam String userName) {
        return tourGuideService.getTripDeals(userName);
    }
}
