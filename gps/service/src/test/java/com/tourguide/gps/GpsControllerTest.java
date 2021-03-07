package com.tourguide.gps;

import com.tourguide.gps.model.Attraction;
import com.tourguide.gps.model.Location;
import com.tourguide.gps.model.NearbyAttraction;
import com.tourguide.gps.model.TrackNearbyAttraction;
import com.tourguide.gps.model.VisitedLocation;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GpsController.class)
class GpsControllerTest {
    private static final UUID USER_ID = UUID.fromString("2c75f574-3855-449e-85a1-f30d2466bf99");

    @MockBean
    private GpsService gpsService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAttractions() throws Exception {
        when(gpsService.getAttractions()).thenReturn(Arrays.asList(
                Attraction.builder().build(),
                Attraction.builder().build()));

        mockMvc.perform(get("/attractions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void getUserLocation() throws Exception {
        when(gpsService.getUserLocation(USER_ID)).thenReturn(Optional.of(VisitedLocation.builder()
                .location(Location.builder().latitude(1D).longitude(2D).build())
                .timeVisited(ZonedDateTime.of(2020, 1, 2, 3, 4, 5, 0, ZoneOffset.UTC))
                .build()));

        mockMvc.perform(get("/userLocation")
                .queryParam("userId", USER_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"location\":{\"latitude\":1.0,\"longitude\":2.0},\"timeVisited\":\"2020-01-02T03:04:05Z\"}"));

        mockMvc.perform(get("/userLocation")
                .queryParam("userId", "1efdceef-91d9-4870-810b-9f812120dfe0"))
                .andExpect(status().isNoContent());

        // TODO: errors
    }

    @Test
    void getNearbyAttractions() throws Exception {
        List<Location> locations = Collections.singletonList(Location.builder().latitude(1D).longitude(2D).build());
        when(gpsService.getNearbyAttractions(locations, 120D, 10))
                .thenReturn(Collections.singletonList(NearbyAttraction.builder()
                        .attraction(Attraction.builder().id(UUID.fromString("a6ecc2f0-ca54-4e02-98e4-60c792b064c6")).build())
                        .location(Location.builder().latitude(1D).longitude(2D).build())
                        .distance(50D)
                        .build()));

        mockMvc.perform(get("/nearbyAttractions")
                .queryParam("location", "1,2")
                .queryParam("maxDistance", "120")
                .queryParam("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"attraction\":{\"id\":\"a6ecc2f0-ca54-4e02-98e4-60c792b064c6\"},\"location\":{\"latitude\":1.0,\"longitude\":2.0},\"distance\":50.0}]"));

        // TODO: errors
    }

    @Test
    void trackNearbyAttractions() throws Exception {
        List<Location> locations = Collections.singletonList(Location.builder().latitude(1D).longitude(2D).build());
        when(gpsService.trackNearbyAttractions(USER_ID, locations, 120D, 10))
                .thenReturn(TrackNearbyAttraction.builder()
                        .userLocation(VisitedLocation.builder()
                                .location(Location.builder().latitude(1D).longitude(2D).build())
                                .timeVisited(ZonedDateTime.of(2020, 1, 2, 3, 4, 5, 0, ZoneOffset.UTC))
                                .build())
                        .nearbyAttractions(Collections.singletonList(NearbyAttraction.builder()
                                .attraction(Attraction.builder().id(UUID.fromString("a6ecc2f0-ca54-4e02-98e4-60c792b064c6")).build())
                                .location(Location.builder().latitude(1D).longitude(2D).build())
                                .distance(50D)
                                .build()))
                        .build());

        mockMvc.perform(get("/trackNearbyAttractions")
                .queryParam("userId", USER_ID.toString())
                .queryParam("location", "1,2")
                .queryParam("maxDistance", "120")
                .queryParam("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"userLocation\":{\"location\":{\"latitude\":1.0,\"longitude\":2.0},\"timeVisited\":\"2020-01-02T03:04:05Z\"},\"nearbyAttractions\":[{\"attraction\":{\"id\":\"a6ecc2f0-ca54-4e02-98e4-60c792b064c6\"},\"location\":{\"latitude\":1.0,\"longitude\":2.0},\"distance\":50.0}]}"));

        // TODO: errors
    }
}