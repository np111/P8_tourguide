package com.tourguide.users.model;

import com.tourguide.gps.model.VisitedLocation;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class User {
    private UUID id;
    private String name;
    private String phoneNumber;
    private String emailAddress;
    private ZonedDateTime latestLocationTimestamp;
    private List<VisitedLocation> visitedLocations;
    private List<UserReward> rewards;
    private UserPreferences preferences;
}
