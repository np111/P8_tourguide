package com.tourguide.users;

import com.tourguide.users.mock.MockConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(MockConfig.class)
class UsersApplicationTest {
    @Test
    void contextLoads() {
    }
}