package com.tourguide.rewards;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.Generated;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.tourguide.rewards.RewardsApplication.NAME;
import static com.tourguide.rewards.RewardsApplication.VERSION;

@SpringBootApplication(scanBasePackages = "com.tourguide")
@OpenAPIDefinition(
        info = @Info(
                title = NAME,
                version = VERSION
        )
)
public class RewardsApplication {
    public static final String NAME = "TourGuide Rewards API";
    public static final String VERSION = "1.0";

    @Generated
    public static void main(String[] args) {
        SpringApplication.run(RewardsApplication.class, args);
    }
}
