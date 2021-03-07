package com.tourguide.gps;

import com.tourguide.gps.mock.MockConfig;
import com.tourguide.gps.model.Attraction;
import com.tourguide.gps.model.Location;
import com.tourguide.gps.model.NearbyAttraction;
import com.tourguide.gps.model.TrackNearbyAttraction;
import com.tourguide.gps.model.VisitedLocation;
import gpsUtil.GpsUtil;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(MockConfig.class)
class GpsServiceTest {
    private static final List<gpsUtil.location.Attraction> RAW_ATTRACTIONS = Arrays.asList(
            new gpsUtil.location.Attraction("Attraction0", "City0", "State0", 1D, 2D),
            new gpsUtil.location.Attraction("Attraction1", "City1", "State1", 3D, 4D));

    private static final List<Attraction> ATTRACTIONS = Arrays.asList(
            Attraction.builder()
                    .id(RAW_ATTRACTIONS.get(0).attractionId)
                    .name("Attraction0")
                    .city("City0")
                    .state("State0")
                    .location(Location.builder().latitude(1D).longitude(2D).build())
                    .build(),
            Attraction.builder()
                    .id(RAW_ATTRACTIONS.get(1).attractionId)
                    .name("Attraction1")
                    .city("City1")
                    .state("State1")
                    .location(Location.builder().latitude(3D).longitude(4D).build())
                    .build());

    private static final UUID USER_ID = UUID.fromString("539673f2-ee6a-4478-b97a-262da51bea34");

    @MockBean
    private GpsUtil gpsUtil;

    @Autowired
    private GpsService gpsService;

    @BeforeEach
    void setUp() {
        when(gpsUtil.getAttractions()).thenAnswer(a -> new ArrayList<>(RAW_ATTRACTIONS));
        gpsService.afterPropertiesSet();
    }

    @Test
    void getAttractions() {
        assertEquals(ATTRACTIONS, gpsService.getAttractions());
        assertThrows(UnsupportedOperationException.class, () -> gpsService.getAttractions().clear());
    }

    @Test
    void getUserLocation() {
        Date nowDate = new Date(System.currentTimeMillis() / 1000L * 1000L);
        when(gpsUtil.getUserLocation(USER_ID)).thenReturn(new gpsUtil.location.VisitedLocation(USER_ID, new gpsUtil.location.Location(1D, 2D), nowDate));

        assertFalse(gpsService.getUserLocation(null).isPresent());

        Optional<VisitedLocation> ret = gpsService.getUserLocation(USER_ID);
        assertTrue(ret.isPresent());
        assertEquals(VisitedLocation.builder()
                .location(Location.builder().latitude(1D).longitude(2D).build())
                .timeVisited(ZonedDateTime.ofInstant(Instant.ofEpochMilli(nowDate.getTime()), ZoneId.systemDefault()))
                .build(), ret.get());
    }

    @Test
    void getNearbyAttractions() {
        Location loc0 = Location.builder().latitude(2.5D).longitude(2.5D).build();
        Location loc1 = Location.builder().latitude(3.5D).longitude(3.5D).build();
        List<Location> locations = Arrays.asList(loc0, loc1);

        List<NearbyAttraction> excepted = Arrays.asList(
                NearbyAttraction.builder()
                        .attraction(ATTRACTIONS.get(1))
                        .location(loc1)
                        .distance(gpsService.getDistanceInMile(ATTRACTIONS.get(1).getLocation(), loc1))
                        .build(),
                NearbyAttraction.builder()
                        .attraction(ATTRACTIONS.get(0))
                        .location(loc0)
                        .distance(gpsService.getDistanceInMile(ATTRACTIONS.get(0).getLocation(), loc0))
                        .build());

        assertEquals(excepted, gpsService.getNearbyAttractions(locations, null, null));
        assertEquals(excepted, gpsService.getNearbyAttractions(locations, 120D, 10));
        assertEquals(excepted.subList(0, 1), gpsService.getNearbyAttractions(locations, null, 1));
        assertEquals(excepted.subList(0, 1), gpsService.getNearbyAttractions(locations, 60D, null));
        assertThrows(IllegalArgumentException.class, () -> gpsService.getNearbyAttractions(new ArrayList<>(), null, null));
    }

    @Test
    void trackNearbyAttractions() {
        GpsService gpsService = Mockito.spy(this.gpsService);
        VisitedLocation userLocation = VisitedLocation.builder()
                .location(Location.builder().latitude(1D).longitude(2D).build())
                .timeVisited(ZonedDateTime.now().withNano(0))
                .build();
        Location miscLocation = Location.builder().latitude(2D).longitude(3D).build();

        doReturn(Optional.of(userLocation)).when(gpsService).getUserLocation(USER_ID);
        doAnswer(a -> {
            List<Location> locations = a.getArgument(0);
            return locations.stream().map(l -> NearbyAttraction.builder()
                    .attraction(Attraction.builder().build())
                    .location(l)
                    .distance(0D)
                    .build()).collect(Collectors.toList());
        }).when(gpsService).getNearbyAttractions(any(), any(), any());

        TrackNearbyAttraction res;

        res = gpsService.trackNearbyAttractions(null, null, null, null);
        assertNull(res.getUserLocation());
        assertTrue(res.getNearbyAttractions().isEmpty());

        res = gpsService.trackNearbyAttractions(null, new ArrayList<>(), null, null);
        assertNull(res.getUserLocation());
        assertTrue(res.getNearbyAttractions().isEmpty());

        res = gpsService.trackNearbyAttractions(USER_ID, null, null, null);
        assertEquals(userLocation, res.getUserLocation());
        assertEquals(Collections.singletonList(userLocation.getLocation()), res.getNearbyAttractions().stream().map(NearbyAttraction::getLocation).collect(Collectors.toList()));

        res = gpsService.trackNearbyAttractions(USER_ID, new ArrayList<>(Collections.singletonList(miscLocation)), null, null);
        assertEquals(userLocation, res.getUserLocation());
        assertEquals(Arrays.asList(miscLocation, userLocation.getLocation()), res.getNearbyAttractions().stream().map(NearbyAttraction::getLocation).collect(Collectors.toList()));
    }

    @Test
    void getDistanceInMile() {
        assertEquals(3429.4552004399566D, gpsService.getDistanceInMile(
                // Eiffel Tower:
                Location.builder().latitude(48.8584D).longitude(2.2945D).build(),
                // Statue of Liberty:
                Location.builder().latitude(40.6892D).longitude(74.0445D).build()));

        assertEquals(0D, gpsService.getDistanceInMile(
                Location.builder().latitude(1D).longitude(2D).build(),
                Location.builder().latitude(1D).longitude(2D).build()));

        assertEquals(195.22929650228747D, gpsService.getDistanceInMile(
                Location.builder().latitude(1D).longitude(2D).build(),
                Location.builder().latitude(3D).longitude(4D).build()));
    }
}