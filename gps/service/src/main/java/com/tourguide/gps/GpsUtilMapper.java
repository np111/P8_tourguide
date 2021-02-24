package com.tourguide.gps;

import com.tourguide.gps.model.Attraction;
import com.tourguide.gps.model.Location;
import com.tourguide.gps.model.VisitedLocation;
import com.tourguide.mapstruct.MapperConfig;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface GpsUtilMapper {
    @Mapping(target = "id", source = "attractionId")
    @Mapping(target = "name", source = "attractionName")
    @Mapping(target = "location", source = ".")
    @BeanMapping(ignoreUnmappedSourceProperties = {"latitude", "longitude"})
    Attraction toAttraction(gpsUtil.location.Attraction entity);

    Location toLocation(gpsUtil.location.Location entity);

    @BeanMapping(ignoreUnmappedSourceProperties = {"userId"})
    VisitedLocation toVisitedLocation(gpsUtil.location.VisitedLocation entity);
}
