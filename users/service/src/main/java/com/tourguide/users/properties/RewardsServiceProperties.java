package com.tourguide.users.properties;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "tourguide.rewards-service")
@Data
@Validated
public class RewardsServiceProperties {
    @NotEmpty
    private String url;

    @NotNull
    @Min(16)
    private Integer maxConcurrentRequests;
}
