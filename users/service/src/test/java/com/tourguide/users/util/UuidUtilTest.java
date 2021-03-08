package com.tourguide.users.util;

import java.util.Random;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UuidUtilTest {
    @Test
    void randomUUID() {
        assertEquals("5c9f20d5-8361-4331-aed8-a921eac80778", UuidUtil.randomUUID(new Random(12345L)).toString());
    }
}