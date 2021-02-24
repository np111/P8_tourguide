package com.tourguide.rewards;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
@Slf4j
public class RewardsService {
    private final RewardCentral rewardCentral;

    public int getAttractionRewardPoints(UUID attractionId, UUID userId) {
        return rewardCentral.getAttractionRewardPoints(attractionId, userId);
    }
}
