package com.tourguide.users.entity;

import java.time.ZonedDateTime;
import lombok.Data;
import lombok.NonNull;

@Data
public class InternalVisitedLocationEntity {
    private final double latitude;
    private final double longitude;
    private final @NonNull ZonedDateTime timeVisited;
}
