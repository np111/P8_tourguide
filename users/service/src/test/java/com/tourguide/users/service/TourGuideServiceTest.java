package com.tourguide.users.service;

import com.tourguide.gps.model.Location;
import com.tourguide.gps.model.VisitedLocation;
import com.tourguide.users.mock.MockConfig;
import com.tourguide.users.model.User;
import com.tourguide.users.model.UserNearbyAttractions;
import com.tourguide.users.util.UserNotFoundException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(MockConfig.class)
class TourGuideServiceTest {
    @MockBean
    private UserService userService;
    @MockBean
    private TripPricerService tripPricerService;

    @Autowired
    private TourGuideService tourGuideService;

    @BeforeEach
    void setUp() {
        when(userService.getUser(anyString())).thenAnswer(a -> {
            String userName = a.getArgument(0);
            if ("user".equals(userName)) {
                return User.builder()
                        .name(userName)
                        .visitedLocations(Arrays.asList(
                                VisitedLocation.builder()
                                        .location(Location.builder().latitude(1D).longitude(2D).build())
                                        .timeVisited(ZonedDateTime.now(ZoneOffset.UTC).withNano(0))
                                        .build(),
                                VisitedLocation.builder()
                                        .location(Location.builder().latitude(3D).longitude(4D).build())
                                        .timeVisited(ZonedDateTime.now(ZoneOffset.UTC).withNano(0))
                                        .build()))
                        .build();
            }
            if ("new-user".equals(userName)) {
                return User.builder()
                        .name(userName)
                        .visitedLocations(Collections.emptyList())
                        .build();
            }
            throw new UserNotFoundException(userName);
        });

        when(tripPricerService.getTripDeals(any())).thenReturn(Collections.emptyList());
    }

    @Test
    void getNearbyAttractions() {
        assertThrows(UserNotFoundException.class, () -> tourGuideService.getNearbyAttractions("unknown"));
        assertFalse(tourGuideService.getNearbyAttractions("new-user").isPresent());

        UserNearbyAttractions ret = tourGuideService.getNearbyAttractions("user").get();
        assertEquals(Location.builder().latitude(3D).longitude(4D).build(), ret.getUserLocation());
        assertFalse(ret.getNearbyAttractions().isEmpty());
    }

    @Test
    void getTripDeals() {
        assertThrows(UserNotFoundException.class, () -> tourGuideService.getTripDeals("unknown"));
        assertEquals(Collections.emptyList(), tourGuideService.getTripDeals("user"));
    }
}