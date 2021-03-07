package com.tourguide.gps.util;

import com.tourguide.gps.model.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringToLocationConverterTest {
    @Test
    void convert() {
        StringToLocationConverter converter = new StringToLocationConverter();
        assertThrows(IllegalArgumentException.class, () -> converter.convert(""));
        assertThrows(IllegalArgumentException.class, () -> converter.convert("1"));
        assertThrows(IllegalArgumentException.class, () -> converter.convert("1,"));
        assertThrows(IllegalArgumentException.class, () -> converter.convert(",2"));
        assertThrows(IllegalArgumentException.class, () -> converter.convert("1,2,3"));
        assertThrows(IllegalArgumentException.class, () -> converter.convert("Infinity,2"));
        assertEquals(Location.builder().latitude(1D).longitude(2D).build(), converter.convert("1,2"));
    }
}