package com.tourguide.users.controller;

import com.tourguide.gps.model.Location;
import com.tourguide.gps.model.VisitedLocation;
import com.tourguide.model.ApiError;
import com.tourguide.model.ApiError.ErrorType;
import com.tourguide.openapi.error.ApiErrorResponse;
import com.tourguide.openapi.response.ApiNullResponse;
import com.tourguide.users.model.TripDeal;
import com.tourguide.users.model.UserNearbyAttractions;
import com.tourguide.users.model.UserReward;
import com.tourguide.users.service.TourGuideService;
import com.tourguide.users.service.UserService;
import com.tourguide.users.util.UserNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class TourGuideController {
    private final UserService userService;
    private final TourGuideService tourGuideService;

    @Operation(summary = "Get the user's last known location")
    @ApiNullResponse(description = "If the user has no known location.")
    @ApiErrorResponse(method = "handleUserNotFoundException")
    @RequestMapping(method = RequestMethod.GET, value = "/getLocation")
    public VisitedLocation getLocation(@RequestParam String userName) throws UserNotFoundException {
        return userService.getUserLocation(userName).orElse(null);
    }

    @Operation(summary = "Lists the user's rewards")
    @ApiErrorResponse(method = "handleUserNotFoundException")
    @RequestMapping(method = RequestMethod.GET, value = "/getRewards")
    public List<UserReward> getRewards(@RequestParam String userName) throws UserNotFoundException {
        return userService.getUserRewards(userName);
    }

    @Operation(
            summary = "Lists all user's last known location",
            description = "Returns a map associating user ID (uuid) to it's last known location (which can be null if unknown)."
    )
    @RequestMapping(method = RequestMethod.GET, value = "/getAllCurrentLocations")
    public Map<UUID, Optional<Location>> getAllCurrentLocations() {
        return userService.getAllCurrentLocations();
    }

    @Operation(
            summary = "Lists the user's closest attractions",
            description = "Returns the list of the 5 attractions closest to the user last known location"
                    + " (sorted from the closest to the farthest)."
    )
    @ApiErrorResponse(method = "handleUserNotFoundException")
    @ApiNullResponse(description = "If the user has no known location.")
    @RequestMapping(method = RequestMethod.GET, value = "/getNearbyAttractions")
    public UserNearbyAttractions getNearbyAttractions(@RequestParam String userName) throws UserNotFoundException {
        return tourGuideService.getNearbyAttractions(userName).orElse(null);
    }

    @Operation(summary = "Lists the user's trip deals")
    @ApiErrorResponse(method = "handleUserNotFoundException")
    @RequestMapping(method = RequestMethod.GET, value = "/getTripDeals")
    public List<TripDeal> getTripDeals(@RequestParam String userName) throws UserNotFoundException {
        return tourGuideService.getTripDeals(userName);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<>(ApiError.builder()
                .type(ErrorType.SERVICE)
                .code("USER_NOT_FOUND")
                .message("The user does not exists")
                .metadata("userName", ex.getUserName())
                .build(), HttpStatus.NOT_FOUND);
    }
}
