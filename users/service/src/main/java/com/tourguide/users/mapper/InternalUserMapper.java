package com.tourguide.users.mapper;

import com.tourguide.gps.model.VisitedLocation;
import com.tourguide.mapstruct.MapperConfig;
import com.tourguide.users.entity.InternalUserEntity;
import com.tourguide.users.entity.InternalUserPreferencesEntity;
import com.tourguide.users.entity.InternalUserRewardEntity;
import com.tourguide.users.entity.InternalVisitedLocationEntity;
import com.tourguide.users.model.Money;
import com.tourguide.users.model.User;
import com.tourguide.users.model.UserPreferences;
import com.tourguide.users.model.UserReward;
import java.math.BigDecimal;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface InternalUserMapper {
    @BeanMapping(ignoreUnmappedSourceProperties = {"latestLocation"})
    User toUser(InternalUserEntity entity);

    @BeanMapping(ignoreUnmappedSourceProperties = {"latestLocationTimestamp"})
    InternalUserEntity fromUser(User model);

    UserPreferences toUserPreferences(InternalUserPreferencesEntity entity);

    @Mapping(target = "location.latitude", source = "latitude")
    @Mapping(target = "location.longitude", source = "longitude")
    VisitedLocation toVisitedLocation(InternalVisitedLocationEntity entity);

    default InternalVisitedLocationEntity fromVisitedLocation(VisitedLocation model) {
        if (model == null || model.getLocation() == null) {
            return null;
        }
        return new InternalVisitedLocationEntity(
                model.getLocation().getLatitude(),
                model.getLocation().getLongitude(),
                model.getTimeVisited());
    }

    UserReward toReward(InternalUserRewardEntity entity);

    InternalUserRewardEntity fromReward(UserReward reward);

    default String map(CurrencyUnit value) {
        return value == null ? null : value.getCurrencyCode();
    }

    default CurrencyUnit map(String value) {
        return value == null ? null : Monetary.getCurrency(value);
    }

    default Money map(org.javamoney.moneta.Money value) {
        return value == null ? null : Money.builder()
                .currency(value.getCurrency().getCurrencyCode())
                .amount(value.getNumberStripped().toPlainString())
                .build();
    }

    default org.javamoney.moneta.Money map(Money value) {
        return value == null ? null : org.javamoney.moneta.Money.of(new BigDecimal(value.getAmount()), value.getCurrency());
    }
}
