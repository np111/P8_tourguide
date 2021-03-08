package com.tourguide.users.service.impl;

import com.google.common.collect.Iterables;
import com.tourguide.gps.model.Attraction;
import com.tourguide.gps.model.Location;
import com.tourguide.gps.model.VisitedLocation;
import com.tourguide.users.mock.MockConfig;
import com.tourguide.users.model.Money;
import com.tourguide.users.model.User;
import com.tourguide.users.model.UserPreferences;
import com.tourguide.users.model.UserReward;
import com.tourguide.users.service.UserService;
import com.tourguide.users.util.UserNotFoundException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import(MockConfig.class)
class InternalUserServiceTest {
    private static final Attraction ATTRACTION_0 = Attraction.builder()
            .id(UUID.fromString("3a84241e-7144-466a-827f-7daf5166464e"))
            .name("Attraction0")
            .build();

    private static final Attraction ATTRACTION_1 = Attraction.builder()
            .id(UUID.fromString("1c061c79-ad0d-4d19-b7ab-c971c8b51afa"))
            .name("Attraction1")
            .build();

    private static final List<User> USERS = Arrays.asList(
            User.builder()
                    .id(UUID.fromString("eb4aa353-48e3-4e64-ac68-970948ab3677"))
                    .name("internalUser0")
                    .phoneNumber("000")
                    .emailAddress("internalUser0@tourGuide.com")
                    .latestLocationTimestamp(ZonedDateTime.of(2021, 2, 19, 5, 11, 5, 0, ZoneOffset.UTC))
                    .visitedLocations(Arrays.asList(
                            VisitedLocation.builder()
                                    .location(Location.builder().latitude(10.9D).longitude(131.5D).build())
                                    .timeVisited(ZonedDateTime.of(2021, 2, 15, 13, 55, 29, 0, ZoneOffset.UTC))
                                    .build(),
                            VisitedLocation.builder()
                                    .location(Location.builder().latitude(32.3D).longitude(125.2D).build())
                                    .timeVisited(ZonedDateTime.of(2021, 2, 19, 5, 11, 5, 0, ZoneOffset.UTC))
                                    .build()))
                    .rewards(Collections.emptyList())
                    .preferences(UserPreferences.builder()
                            .attractionProximity(Integer.MAX_VALUE)
                            .currency("USD")
                            .lowerPricePoint(Money.builder().currency("USD").amount("0").build())
                            .highPricePoint(Money.builder().currency("USD").amount("" + Integer.MAX_VALUE).build())
                            .tripDuration(1)
                            .ticketQuantity(1)
                            .numberOfAdults(1)
                            .numberOfChildren(0)
                            .build())
                    .build(),
            User.builder()
                    .id(UUID.fromString("024b2fae-86e5-4c77-abbd-d5ddfd2497aa"))
                    .name("internalUser1")
                    .phoneNumber("123")
                    .emailAddress("internalUser1@tourGuide.com")
                    .latestLocationTimestamp(null)
                    .visitedLocations(Collections.emptyList())
                    .rewards(Collections.emptyList())
                    .preferences(UserPreferences.builder()
                            .attractionProximity(Integer.MAX_VALUE)
                            .currency("EUR")
                            .lowerPricePoint(Money.builder().currency("EUR").amount("1").build())
                            .highPricePoint(Money.builder().currency("EUR").amount("100").build())
                            .tripDuration(2)
                            .ticketQuantity(3)
                            .numberOfAdults(4)
                            .numberOfChildren(5)
                            .build())
                    .build());

    static {
        VisitedLocation visitedLocation = USERS.get(0).getVisitedLocations().get(0);
        USERS.get(0).setRewards(Arrays.asList(
                UserReward.builder().attraction(ATTRACTION_0).visitedLocation(visitedLocation).rewardPoints(1).build(),
                UserReward.builder().attraction(ATTRACTION_1).visitedLocation(visitedLocation).rewardPoints(1).build()));
    }

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService.clearUsers();
        USERS.forEach(user -> userService.addUser(user));
    }

    @Test
    void getAllUsers() {
        assertEquals(USERS, userService.getAllUsers().stream()
                .sorted(Comparator.comparing(User::getName)).collect(Collectors.toList()));
    }

    @Test
    void getAllCurrentLocations() {
        Map<UUID, Optional<Location>> excepted = new HashMap<>();
        USERS.forEach(user -> {
            VisitedLocation location = Iterables.getLast(user.getVisitedLocations(), null);
            excepted.put(user.getId(), Optional.ofNullable(location == null ? null : location.getLocation()));
        });
        assertEquals(excepted, userService.getAllCurrentLocations());
    }

    @Test
    void addUser() {
        assertFalse(userService.addUser(USERS.get(0)));
        userService.clearUsers();
        assertTrue(userService.addUser(USERS.get(0)));
    }

    @Test
    void getUser() {
        assertEquals(USERS.get(0), userService.getUser("internalUser0"));
        assertEquals(USERS.get(1), userService.getUser("internalUser1"));
        assertThrows(UserNotFoundException.class, () -> userService.getUser("internalUser2"));
    }

    @Test
    void getUserId() {
        assertEquals(USERS.get(0).getId(), userService.getUserId("internalUser0"));
        assertEquals(USERS.get(1).getId(), userService.getUserId("internalUser1"));
        assertThrows(UserNotFoundException.class, () -> userService.getUser("internalUser2"));
    }

    @Test
    void getUserLocation() {
        assertEquals(Optional.of(Iterables.getLast(USERS.get(0).getVisitedLocations())), userService.getUserLocation("internalUser0"));
        assertEquals(Optional.empty(), userService.getUserLocation("internalUser1"));
        assertThrows(UserNotFoundException.class, () -> userService.getUser("internalUser2"));
    }

    @Test
    void getUserRewards() {
        assertEquals(USERS.get(0).getRewards(), userService.getUserRewards("internalUser0"));
        assertEquals(Collections.emptyList(), userService.getUserRewards("internalUser1"));
        assertThrows(UserNotFoundException.class, () -> userService.getUser("internalUser2"));
    }

    @Test
    void registerVisitedLocation() {
        // registerVisitedLocation
        String userName = USERS.get(0).getName();
        VisitedLocation visitedLocation = VisitedLocation.builder()
                .location(Location.builder().latitude(1D).longitude(2D).build())
                .timeVisited(ZonedDateTime.now(ZoneOffset.UTC).withNano(0))
                .build();
        userService.registerVisitedLocation(userName, visitedLocation);
        assertEquals(visitedLocation, Iterables.getLast(userService.getUser(userName).getVisitedLocations()));
        assertEquals(visitedLocation.getTimeVisited(), userService.getUser(userName).getLatestLocationTimestamp());

        // registerVisitedLocation limit
        for (int i = 0; i < 100; ++i) {
            userService.registerVisitedLocation(userName, visitedLocation);
        }
        assertEquals(Collections.nCopies(50, visitedLocation), userService.getUser(userName).getVisitedLocations());

        // non-null
        assertThrows(NullPointerException.class, () -> userService.registerVisitedLocation(userName, null));
    }

    @Test
    void registerReward() {
        String userName = USERS.get(1).getName();
        UserReward reward0 = USERS.get(0).getRewards().get(0);
        UserReward reward1 = USERS.get(0).getRewards().get(1);

        assertTrue(userService.registerReward(userName, reward0));
        assertFalse(userService.registerReward(userName, reward0));
        assertTrue(userService.registerReward(userName, reward1));
        assertFalse(userService.registerReward(userName, reward1));

        assertEquals(Arrays.asList(reward0, reward1), userService.getUserRewards(userName).stream()
                .sorted(Comparator.comparing(a -> a.getAttraction().getName())).collect(Collectors.toList()));
    }

    @Test
    void hasRewardForAttraction() {
        assertTrue(userService.hasRewardForAttraction(USERS.get(0).getName(), ATTRACTION_0.getName()));
        assertTrue(userService.hasRewardForAttraction(USERS.get(0).getName(), ATTRACTION_1.getName()));
        assertFalse(userService.hasRewardForAttraction(USERS.get(1).getName(), ATTRACTION_0.getName()));
        assertFalse(userService.hasRewardForAttraction(USERS.get(1).getName(), ATTRACTION_1.getName()));
    }
}