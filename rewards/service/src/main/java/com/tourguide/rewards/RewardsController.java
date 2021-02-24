package com.tourguide.rewards;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@Validated
public class RewardsController {
    private final RewardsService rewardsService;

    @RequestMapping(value = "/getAttractionRewardPoints")
    public int getUserLocation(
            @RequestParam(name = "attractionId") UUID attractionId,
            @RequestParam(name = "userId") UUID userId
    ) {
        return rewardsService.getAttractionRewardPoints(attractionId, userId);
    }
}
