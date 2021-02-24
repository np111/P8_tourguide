package com.tourguide.users.util;

import java.util.Random;
import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UuidUtil {
    /**
     * Retrieve a type 4 (pseudo randomly generated) UUID.
     *
     * @return A randomly generated UUID
     */
    public static UUID randomUUID(Random random) {
        return new UUID(
                random.nextLong() & 0xFFFFFFFFFFFF0FFFL | 0x0000000000004000L,
                random.nextLong() & 0x3FFFFFFFFFFFFFFFL | 0x8000000000000000L);
    }
}
