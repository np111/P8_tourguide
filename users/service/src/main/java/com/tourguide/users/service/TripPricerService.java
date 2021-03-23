package com.tourguide.users.service;

import com.tourguide.users.model.Money;
import com.tourguide.users.model.TripDeal;
import com.tourguide.users.model.User;
import com.tourguide.users.model.UserReward;
import com.tourguide.users.properties.TripPricerServiceProperties;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tripPricer.TripPricer;

/**
 * Trip pricer management service.
 */
@Service
public class TripPricerService {
    private static final String TRIP_PRICER_CURRENCY = "USD";

    private final TripPricer tripPricer;

    /**
     * Secret key to communicate with the TripPricer API.
     */
    private final String apiKey;

    @Autowired
    public TripPricerService(TripPricerServiceProperties props, TripPricer tripPricer) {
        this.tripPricer = tripPricer;
        this.apiKey = props.getApiKey();
    }

    /**
     * Returns the trip deals list for a user, according to his preferences.
     *
     * @param user the user
     * @return the trip deals list
     */
    public List<TripDeal> getTripDeals(User user) {
        int totalRewardPoints = user.getRewards().stream().mapToInt(UserReward::getRewardPoints).sum();
        return tripPricer.getPrice(
                apiKey,
                user.getId(),
                user.getPreferences().getNumberOfAdults(),
                user.getPreferences().getNumberOfChildren(),
                user.getPreferences().getTripDuration(),
                totalRewardPoints)
                .stream()
                .map(e -> TripDeal.builder()
                        .tripId(e.tripId)
                        .name(e.name)
                        .price(Money.builder()
                                .currency(TRIP_PRICER_CURRENCY)
                                .amount(BigDecimal.valueOf(e.price).toPlainString())
                                .build())
                        .build())
                .collect(Collectors.toList());
    }
}
