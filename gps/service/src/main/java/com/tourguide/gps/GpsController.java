package com.tourguide.gps;

import com.tourguide.gps.model.Attraction;
import com.tourguide.gps.model.Location;
import com.tourguide.gps.model.NearbyAttraction;
import com.tourguide.gps.model.TrackNearbyAttraction;
import com.tourguide.gps.model.VisitedLocation;
import com.tourguide.openapi.response.ApiNullResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collections;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "gps", description = "GPS operations")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@Validated
public class GpsController {
    private final GpsService gpsService;

    @Operation(
            summary = "Lists the attractions",
            description = "Returns the list of all attractions."
    )
    @RequestMapping(method = RequestMethod.GET, value = "/attractions")
    public List<Attraction> getAttractions() {
        return gpsService.getAttractions();
    }

    @Operation(summary = "Retrieve the user's current location")
    @ApiNullResponse(description = "If it is not possible to locate the specified user (e.g. if he does not exist).")
    @RequestMapping(method = RequestMethod.GET, value = "/userLocation")
    public VisitedLocation getUserLocation(@RequestParam(name = "userId") UUID userId) {
        return gpsService.getUserLocation(userId).orElse(null);
    }

    @Operation(
            summary = "Lists the nearest attractions",
            description = "Returns the list of attractions closest to the specified locations"
                    + " (if more than one location is specified, then for each attraction only the closest one is considered)."
                    + "<br/>\nThe list is sorted by distance (from nearest to farthest)."
                    + "<br/>\nYou can also specify a maximum distance and limit the number of results."
    )
    @RequestMapping(method = RequestMethod.GET, value = "/nearbyAttractions")
    public List<NearbyAttraction> getNearbyAttractions(
            @Parameter(
                    schema = @Schema(type = "string", format = "location", example = "33.817595,-117.922008"),
                    description = "<i>Format</i> : <code>&lt;latitude&gt;,&lt;longitude&gt;</code>"
                            + "\n<br/><i>Repeatable</i> (can be repeated to check multiple locations - eg. <code>?location=A&location=B</code>)"
            )
            @RequestParam(name = "location") @NotEmpty List<@NotNull Location> locations,

            @Parameter(description = "Unlimited if not defined.")
            @RequestParam(name = "maxDistance", required = false) @DecimalMin(value = "0", inclusive = false) Double maxDistance,

            @Parameter(description = "Unlimited if not defined.")
            @RequestParam(name = "limit", required = false) @Min(1) Integer limit
    ) {
        return gpsService.getNearbyAttractions(locations, maxDistance, limit);
    }

    @Operation(
            summary = "Retrieve the user's current location and lists the nearest attractions",
            description = "Returns the list of attractions closest to the current user's location (and also the additional locations specified -"
                    + " so if more than one location is specified, then for each attraction only the closest one is considered)."
                    + "<br/>\nThe list is sorted by distance (from nearest to farthest)."
                    + "<br/>\nYou can also specify a maximum distance and limit the number of results."
                    + "<br/>\n"
                    + "<br/>\nThis method is a combination of /userLocation and /nearbyAttractions (to reduce API calls)."
    )
    @RequestMapping(method = RequestMethod.GET, value = "/trackNearbyAttractions")
    public TrackNearbyAttraction trackNearbyAttractions(
            @RequestParam(name = "userId") UUID userId,

            @Parameter(
                    schema = @Schema(type = "string", format = "location", example = "33.817595,-117.922008"),
                    description = "<i>Format</i> : <code>&lt;latitude&gt;,&lt;longitude&gt;</code>"
                            + "\n<br/><i>Repeatable</i> (can be repeated to check multiple locations - eg. <code>?location=A&location=B</code>)"
            )
            @RequestParam(name = "location", required = false) List<@NotNull Location> locations,

            @Parameter(description = "Unlimited if not defined.")
            @RequestParam(name = "maxDistance", required = false) @DecimalMin(value = "0", inclusive = false) Double maxDistance,

            @Parameter(description = "Unlimited if not defined.")
            @RequestParam(name = "limit", required = false) @Min(1) Integer limit
    ) {
        VisitedLocation userLocation = gpsService.getUserLocation(userId).orElse(null);
        if (userLocation != null) {
            if (locations == null) {
                locations = Collections.singletonList(userLocation.getLocation());
            } else {
                locations.add(userLocation.getLocation());
            }
        }

        List<NearbyAttraction> nearbyAttractions;
        if (locations == null || locations.isEmpty()) {
            nearbyAttractions = Collections.emptyList();
        } else {
            nearbyAttractions = gpsService.getNearbyAttractions(locations, maxDistance, limit);
        }

        return TrackNearbyAttraction.builder()
                .userLocation(userLocation)
                .nearbyAttractions(nearbyAttractions)
                .build();
    }
}
