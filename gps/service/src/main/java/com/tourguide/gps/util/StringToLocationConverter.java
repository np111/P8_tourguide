package com.tourguide.gps.util;

import com.tourguide.gps.model.Location;
import org.springframework.core.convert.converter.Converter;

public class StringToLocationConverter implements Converter<String, Location> {
    @Override
    public Location convert(String value) {
        String[] val = value.split(",", 2);
        if (val.length < 2) {
            throw new IllegalArgumentException("A location must be formatted as: \"<latitude>,<longitude>\"");
        }
        Location ret = new Location();
        ret.setLatitude(validateFinite("latitude", Double.parseDouble(val[0])));
        ret.setLongitude(validateFinite("longitude", Double.parseDouble(val[1])));
        return ret;
    }

    private double validateFinite(String name, double val) {
        if (!Double.isFinite(val)) {
            throw new IllegalArgumentException(name + " must be a finite value");
        }
        return val;
    }
}
