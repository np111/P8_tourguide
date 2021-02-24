package com.tourguide.users.model;

import com.tourguide.gps.model.Attraction;
import com.tourguide.gps.model.VisitedLocation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class UserReward {
    private VisitedLocation visitedLocation;
    private Attraction attraction;
    private Integer rewardPoints;
}
