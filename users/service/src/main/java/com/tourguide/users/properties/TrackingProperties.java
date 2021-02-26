package com.tourguide.users.properties;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "tourguide.tracking")
@Data
@Validated
public class TrackingProperties {
    @NotNull
    @Min(0)
    private Double proximityBuffer = 10.0D;

    private boolean warmup = false;
}
