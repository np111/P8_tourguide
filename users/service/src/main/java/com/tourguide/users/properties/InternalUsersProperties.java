package com.tourguide.users.properties;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "tourguide.internal-users")
@Data
@Validated
public class InternalUsersProperties {
    @NotNull
    @Min(1)
    private Integer number;
}
