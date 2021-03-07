package com.tourguide.gps;

import com.tourguide.gps.mock.MockConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(MockConfig.class)
class GpsApplicationTest {
    @Test
    void contextLoads() {
    }
}
