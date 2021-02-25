package com.tourguide.users.properties;

import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "tourguide.trip-pricer-service")
@Data
@Validated
public class TripPricerServiceProperties {
    @NotEmpty
    private String apiKey = "test-server-api-key";
}
