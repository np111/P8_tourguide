package com.tourguide.gps.model;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class TrackNearbyAttraction {
    private VisitedLocation userLocation;
    private List<NearbyAttraction> nearbyAttractions;
}
