package com.tourguide.users.service;

import com.tourguide.gps.model.Location;
import com.tourguide.gps.model.VisitedLocation;
import com.tourguide.users.model.User;
import com.tourguide.users.model.UserReward;
import com.tourguide.users.util.UserNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    List<User> getAllUsers();

    Map<UUID, Optional<Location>> getAllCurrentLocations();

    void clearUsers();

    boolean addUser(User user);

    User getUser(String userName) throws UserNotFoundException;

    UUID getUserId(String userName) throws UserNotFoundException;

    Optional<VisitedLocation> getUserLocation(String userName) throws UserNotFoundException;

    List<UserReward> getUserRewards(String userName) throws UserNotFoundException;

    void registerVisitedLocation(String userName, VisitedLocation visitedLocation) throws UserNotFoundException;

    boolean registerReward(String userName, UserReward reward) throws UserNotFoundException;

    boolean hasRewardForAttraction(String userName, String attractionName) throws UserNotFoundException;
}
