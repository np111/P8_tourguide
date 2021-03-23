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

/**
 * User management service.
 */
public interface UserService {
    /**
     * Returns the list of all users (as a mutable copy).
     */
    List<User> getAllUsers();

    /**
     * Returns the position of all users associated to their ID.
     */
    Map<UUID, Optional<Location>> getAllCurrentLocations();

    /**
     * Delete all users.
     */
    void clearUsers();

    /**
     * Adds a new user.
     *
     * @param user new user to add
     * @return {@code true} if the user has been added; or {@code false} if a user with the same ID already exists
     */
    boolean addUser(User user);

    /**
     * Returns a user by its name.
     *
     * @param userName the name of the user to be return
     * @throws UserNotFoundException if no user match this name.
     */
    User getUser(String userName) throws UserNotFoundException;

    /**
     * Returns a user's ID by its name.
     *
     * @param userName the name of the user to be return the ID
     * @throws UserNotFoundException if no user match this name.
     */
    UUID getUserId(String userName) throws UserNotFoundException;

    /**
     * Returns a user's location by its name.
     *
     * @param userName the name of the user to be return the location
     * @throws UserNotFoundException if no user match this name.
     */
    Optional<VisitedLocation> getUserLocation(String userName) throws UserNotFoundException;

    /**
     * Returns a user's rewards by its name.
     *
     * @param userName the name of the user to be return the rewards
     * @throws UserNotFoundException if no user match this name.
     */
    List<UserReward> getUserRewards(String userName) throws UserNotFoundException;

    /**
     * Registers a new location for a user.
     *
     * @param userName        the name of the user
     * @param visitedLocation the new location
     * @throws UserNotFoundException if no user match this name.
     */
    void registerVisitedLocation(String userName, VisitedLocation visitedLocation) throws UserNotFoundException;

    /**
     * Registers a new reward for a user.
     *
     * @param userName the name of the user
     * @param reward   the new reward
     * @return {@code true} if the reward was registered (because the user had not already received one for this
     * attraction); otherwise {@code false}
     * @throws UserNotFoundException if no user match this name.
     */
    boolean registerReward(String userName, UserReward reward) throws UserNotFoundException;

    /**
     * Checks if a user has already received a reward for a specific attraction.
     *
     * @param userName       the name of the user
     * @param attractionName the name of the attraction to check
     * @throws UserNotFoundException if no user match this name.
     */
    boolean hasRewardForAttraction(String userName, String attractionName) throws UserNotFoundException;
}
