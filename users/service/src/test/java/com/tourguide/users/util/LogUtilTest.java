package com.tourguide.users.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogUtilTest {
    @Test
    void formatMillis() {
        assertEquals("0.000s", LogUtil.formatMillis(0L));
        assertEquals("0.001s", LogUtil.formatMillis(1L));
        assertEquals("0.999s", LogUtil.formatMillis(999L));
        assertEquals("1.000s", LogUtil.formatMillis(1000L));
        assertEquals("123456.789s", LogUtil.formatMillis(123456789L));
        assertEquals("-123456.789s", LogUtil.formatMillis(-123456789L));
    }
}