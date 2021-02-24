package com.tourguide.gps;

import com.tourguide.gps.model.Attraction;
import com.tourguide.gps.model.Location;
import com.tourguide.gps.model.NearbyAttraction;
import com.tourguide.gps.model.TrackNearbyAttraction;
import com.tourguide.gps.model.VisitedLocation;
import java.util.List;
import java.util.UUID;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@Validated
public class GpsController {
    private final GpsService gpsService;

    @RequestMapping("/attractions")
    public List<Attraction> getAttractions() {
        return gpsService.getAttractions();
    }

    @RequestMapping(value = "/userLocation")
    public VisitedLocation getUserLocation(@RequestParam(name = "userId") UUID userId) {
        return gpsService.getUserLocation(userId);
    }

    @RequestMapping(value = "/nearbyAttractions")
    public List<NearbyAttraction> getNearbyAttractions(
            @RequestParam(name = "location") @NotEmpty List<@NotNull Location> locations,
            @RequestParam(name = "maxDistance", required = false) @DecimalMin(value = "0", inclusive = false) Double maxDistance,
            @RequestParam(name = "limit", required = false) @Min(1) Integer limit
    ) {
        return gpsService.getNearbyAttractions(locations, maxDistance, limit);
    }

    @RequestMapping(value = "/trackNearbyAttractions")
    public TrackNearbyAttraction trackNearbyAttractions(
            @RequestParam(name = "userId") UUID userId,
            @RequestParam(name = "location") @NotEmpty List<@NotNull Location> locations,
            @RequestParam(name = "maxDistance", required = false) @DecimalMin(value = "0", inclusive = false) Double maxDistance,
            @RequestParam(name = "limit", required = false) @Min(1) Integer limit
    ) {
        VisitedLocation userLocation = gpsService.getUserLocation(userId);
        locations.add(userLocation.getLocation());
        List<NearbyAttraction> nearbyAttractions = gpsService.getNearbyAttractions(locations, maxDistance, limit);

        return TrackNearbyAttraction.builder()
                .userLocation(userLocation)
                .nearbyAttractions(nearbyAttractions)
                .build();
    }
}
