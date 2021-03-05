package com.tourguide.gps;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import static com.tourguide.gps.GpsApplication.NAME;
import static com.tourguide.gps.GpsApplication.VERSION;

@SpringBootApplication(scanBasePackages = "com.tourguide")
@EnableScheduling
@OpenAPIDefinition(
        info = @Info(
                title = NAME,
                version = VERSION
        )
)
public class GpsApplication {
    public static final String NAME = "TourGuide GPS API";
    public static final String VERSION = "1.0";

    public static void main(String[] args) throws ClassNotFoundException {
        SpringApplication.run(GpsApplication.class, args);
    }
}
