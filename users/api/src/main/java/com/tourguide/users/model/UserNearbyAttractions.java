package com.tourguide.users.model;

import com.tourguide.gps.model.Location;
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
public class UserNearbyAttractions {
    private Location userLocation;
    private List<UserNearbyAttraction> nearbyAttractions;
}
