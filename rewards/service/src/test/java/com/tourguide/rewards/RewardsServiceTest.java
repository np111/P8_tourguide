package com.tourguide.rewards;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import rewardCentral.RewardCentral;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class RewardsServiceTest {
    private static final UUID ATTRACTION_ID = UUID.fromString("215a9d2d-053c-44a2-ab5c-a589c547ee6b");
    private static final UUID USER_ID = UUID.fromString("2c75f574-3855-449e-85a1-f30d2466bf99");
    private static final int REWARDS_POINTS = 484698;

    @MockBean
    private RewardCentral rewardCentral;
    @Autowired
    private RewardsService rewardsService;

    @Test
    void getAttractionRewardPoints() {
        when(rewardCentral.getAttractionRewardPoints(ATTRACTION_ID, USER_ID)).thenReturn(REWARDS_POINTS);

        assertEquals(-1, rewardsService.getAttractionRewardPoints(null, null));
        assertEquals(-1, rewardsService.getAttractionRewardPoints(null, USER_ID));
        assertEquals(-1, rewardsService.getAttractionRewardPoints(ATTRACTION_ID, null));
        assertEquals(REWARDS_POINTS, rewardsService.getAttractionRewardPoints(ATTRACTION_ID, USER_ID));
    }
}