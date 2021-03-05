package com.tourguide.users;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import static com.tourguide.users.UsersApplication.*;

@SpringBootApplication(scanBasePackages = "com.tourguide")
@ConfigurationPropertiesScan(basePackages = "com.tourguide.users.properties")
@EnableScheduling
@OpenAPIDefinition(
        info = @Info(
                title = NAME,
                version = VERSION
        )
)
public class UsersApplication {
    public static final String NAME = "TourGuide Users API";
    public static final String VERSION = "1.0";

    public static void main(String[] args) {
        SpringApplication.run(UsersApplication.class, args);
    }
}
