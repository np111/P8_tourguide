package com.tourguide.rewards;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RewardsController.class)
class RewardsControllerTest {
    private static final UUID ATTRACTION_ID = UUID.fromString("215a9d2d-053c-44a2-ab5c-a589c547ee6b");
    private static final UUID USER_ID = UUID.fromString("2c75f574-3855-449e-85a1-f30d2466bf99");
    private static final int REWARDS_POINTS = 484698;

    @MockBean
    private RewardsService rewardsService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getUserLocation() throws Exception {
        when(rewardsService.getAttractionRewardPoints(ATTRACTION_ID, USER_ID)).thenReturn(REWARDS_POINTS);

        mockMvc.perform(get("/getAttractionRewardPoints")
                .queryParam("attractionId", ATTRACTION_ID.toString())
                .queryParam("userId", USER_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(REWARDS_POINTS));

        // TODO: errors
    }
}