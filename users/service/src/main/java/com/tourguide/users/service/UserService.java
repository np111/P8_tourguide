package com.tourguide.users.service;

import com.tourguide.gps.model.Location;
import com.tourguide.gps.model.VisitedLocation;
import com.tourguide.users.model.User;
import com.tourguide.users.model.UserReward;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    List<User> getAllUsers();

    Map<UUID, Location> getAllCurrentLocations();

    boolean addUser(User user);

    Optional<User> getUser(String userName);

    Optional<UUID> getUserId(String userName);

    Optional<VisitedLocation> getUserLocation(String userName);

    List<UserReward> getUserRewards(String userName);

    boolean registerVisitedLocation(String userName, VisitedLocation visitedLocation);

    boolean registerReward(String userName, UserReward reward);

    boolean hasRewardForAttraction(String userName, String attractionName);
}
