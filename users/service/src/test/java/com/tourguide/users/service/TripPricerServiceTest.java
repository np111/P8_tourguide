package com.tourguide.users.service;

import com.tourguide.users.model.Money;
import com.tourguide.users.model.TripDeal;
import com.tourguide.users.model.User;
import com.tourguide.users.model.UserPreferences;
import com.tourguide.users.model.UserReward;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tripPricer.Provider;
import tripPricer.TripPricer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class TripPricerServiceTest {
    private static final UUID USER_ID = UUID.fromString("8d91f2af-b5e9-427e-8fbc-57aa125599d3");
    private static final UUID TRIP_ID = UUID.fromString("aca7d751-6ee6-4a1a-bf8c-ce6208546ea7");

    @MockBean
    private TripPricer tripPricer;

    @Autowired
    private TripPricerService tripPricerService;

    @Test
    void getTripDeals() {
        when(tripPricer.getPrice(any(), any(), anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(new Provider(TRIP_ID, "name", 1D)));

        User user = User.builder()
                .id(USER_ID)
                .name("name")
                .preferences(UserPreferences.builder()
                        .numberOfAdults(1)
                        .numberOfChildren(2)
                        .tripDuration(3)
                        .build())
                .rewards(Arrays.asList(
                        UserReward.builder()
                                .rewardPoints(10)
                                .build(),
                        UserReward.builder()
                                .rewardPoints(20)
                                .build()))
                .build();

        assertEquals(Collections.singletonList(TripDeal.builder()
                .tripId(TRIP_ID)
                .name("name")
                .price(Money.builder()
                        .currency("USD")
                        .amount("1.0")
                        .build())
                .build()),
                tripPricerService.getTripDeals(user));

        verify(tripPricer, times(1)).getPrice(any(), eq(USER_ID), eq(1), eq(2), eq(3), eq(30));
    }
}