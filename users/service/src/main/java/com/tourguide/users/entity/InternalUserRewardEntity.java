package com.tourguide.users.entity;

import com.tourguide.gps.model.Attraction;
import lombok.Data;

@Data
public class InternalUserRewardEntity {
    private final InternalVisitedLocationEntity visitedLocation;
    private final Attraction attraction;
    private final int rewardPoints;
}
