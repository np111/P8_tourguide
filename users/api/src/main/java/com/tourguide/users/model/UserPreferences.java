package com.tourguide.users.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class UserPreferences {
    private Integer attractionProximity;
    private String currency;
    private Money lowerPricePoint;
    private Money highPricePoint;
    private Integer tripDuration;
    private Integer ticketQuantity;
    private Integer numberOfAdults;
    private Integer numberOfChildren;
}
