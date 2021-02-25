package com.tourguide.users.util;

import java.text.DecimalFormat;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LogUtil {
    private static final DecimalFormat SECS_FORMAT = new DecimalFormat("0.000");

    public static String formatMillis(long millis) {
        return SECS_FORMAT.format(millis / 1000.0D) + "s";
    }
}
