package com.tourguide.rewards;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@Validated
public class RewardsController {
    private final RewardsService rewardsService;

    @Operation(
            summary = "Get rewards points for visiting an attraction",
            description = "Returns the number of points the user can earn if he visit the attraction."
    )
    @ApiResponse(
            description = "OK"
                    + "<br/>\nNote: Returns -1 if the attraction or user does not exists."
    )
    @RequestMapping(method = RequestMethod.GET, value = "/getAttractionRewardPoints")
    public int getUserLocation(
            @RequestParam(name = "attractionId") UUID attractionId,
            @RequestParam(name = "userId") UUID userId
    ) {
        return rewardsService.getAttractionRewardPoints(attractionId, userId);
    }
}
