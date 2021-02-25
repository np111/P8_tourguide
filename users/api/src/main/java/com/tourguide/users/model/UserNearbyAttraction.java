package com.tourguide.users.model;

import com.tourguide.gps.model.Attraction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class UserNearbyAttraction {
    private Attraction attraction;
    private Double distance;
    private Integer rewardPoints;
}
