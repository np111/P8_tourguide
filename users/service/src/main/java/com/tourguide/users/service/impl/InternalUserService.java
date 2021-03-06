package com.tourguide.users.service.impl;

import com.google.common.annotations.VisibleForTesting;
import com.tourguide.gps.model.Location;
import com.tourguide.gps.model.VisitedLocation;
import com.tourguide.users.entity.InternalUserEntity;
import com.tourguide.users.entity.InternalVisitedLocationEntity;
import com.tourguide.users.mapper.InternalUserMapper;
import com.tourguide.users.model.User;
import com.tourguide.users.model.UserReward;
import com.tourguide.users.properties.InternalUsersProperties;
import com.tourguide.users.service.UserService;
import com.tourguide.users.util.UserNotFoundException;
import com.tourguide.users.util.UuidUtil;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InternalUserService implements UserService {
    private static final long GENERATE_RANDOM_SEED = 234648427414755688L;
    private static final int VISITED_LOCATION_LIMIT = 50;

    private final InternalUserMapper internalUserMapper;

    /**
     * This map is thread-safe.
     * Any contained InternalUserEntity must necessarily be synchronized with itself for any reading or writing (so use
     * {@link #withUserLocked} to access them).
     */
    private Map<String, InternalUserEntity> internalUserMap;

    @Autowired
    public InternalUserService(InternalUsersProperties props, InternalUserMapper internalUserMapper) {
        this.internalUserMapper = internalUserMapper;
        this.internalUserMap = generateUserMap(props.getNumber());
    }

    @VisibleForTesting
    public void setUsersNumber(int usersNumber) {
        this.internalUserMap = generateUserMap(usersNumber);
    }

    private Map<String, InternalUserEntity> generateUserMap(int usersNumber) {
        Random random = new Random(GENERATE_RANDOM_SEED);
        Map<String, InternalUserEntity> res = new ConcurrentHashMap<>(usersNumber, 0.5F);
        for (int i = 0; i < usersNumber; ++i) {
            String userName = "internalUser" + i;
            String phone = "000";
            String email = userName + "@tourGuide.com";
            InternalUserEntity user = new InternalUserEntity(UuidUtil.randomUUID(random), userName, phone, email);
            generateUserLocationHistory(user, random);
            res.put(user.getName(), user);
        }
        return res;
    }

    private void generateUserLocationHistory(InternalUserEntity user, Random random) {
        IntStream.range(0, 3)
                .mapToObj(ignored -> generateVisitedLocation(random))
                .sorted(Comparator.comparing(VisitedLocation::getTimeVisited))
                .forEach(visitedLocation -> registerVisitedLocation0(user, visitedLocation));
    }

    private VisitedLocation generateVisitedLocation(Random random) {
        return VisitedLocation.builder()
                .location(Location.builder()
                        .latitude(generateRandomLatitude(random))
                        .longitude(generateRandomLongitude(random))
                        .build())
                .timeVisited(getRandomTime(random))
                .build();
    }

    private double generateRandomLatitude(Random random) {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + random.nextDouble() * (rightLimit - leftLimit);
    }

    private double generateRandomLongitude(Random random) {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + random.nextDouble() * (rightLimit - leftLimit);
    }

    private ZonedDateTime getRandomTime(Random random) {
        return ZonedDateTime.now(ZoneOffset.UTC)
                .minusSeconds(random.nextInt(Math.toIntExact(TimeUnit.DAYS.toSeconds(30))));
    }

    private <T> T withUserLocked(String name, Function<InternalUserEntity, T> fn) throws UserNotFoundException {
        InternalUserEntity user = internalUserMap.get(name);
        if (user == null) {
            throw new UserNotFoundException(name);
        }
        return withUserLocked(user, fn);
    }

    private <T> T withUserLocked(InternalUserEntity user, Function<InternalUserEntity, T> fn) {
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (user) {
            return fn.apply(user);
        }
    }

    @Override
    public List<User> getAllUsers() {
        return internalUserMap.values()
                .stream()
                .map(user -> withUserLocked(user, internalUserMapper::toUser))
                .collect(Collectors.toList());
    }

    @Override
    public Map<UUID, Optional<Location>> getAllCurrentLocations() {
        Map<UUID, Optional<Location>> ret = new HashMap<>(internalUserMap.size());
        internalUserMap.values().forEach(e -> withUserLocked(e,
                user -> ret.put(user.getId(), user.getLatestLocation().map(internalUserMapper::toLocation))));
        return ret;
    }

    @Override
    public void clearUsers() {
        internalUserMap.clear();
    }

    @Override
    public boolean addUser(User user) {
        return internalUserMap.putIfAbsent(user.getName(), internalUserMapper.fromUser(user)) == null;
    }

    @Override
    public User getUser(String userName) throws UserNotFoundException {
        return withUserLocked(userName, internalUserMapper::toUser);
    }

    @Override
    public UUID getUserId(String userName) throws UserNotFoundException {
        return withUserLocked(userName, InternalUserEntity::getId);
    }

    @Override
    public Optional<VisitedLocation> getUserLocation(String userName) throws UserNotFoundException {
        return withUserLocked(userName, user -> user.getLatestLocation().map(internalUserMapper::toVisitedLocation));
    }

    @Override
    public List<UserReward> getUserRewards(String userName) throws UserNotFoundException {
        return withUserLocked(userName, user -> user.getRewards().stream().map(internalUserMapper::toReward).collect(Collectors.toList()));
    }

    @Override
    public void registerVisitedLocation(String userName, VisitedLocation visitedLocation) throws UserNotFoundException {
        withUserLocked(userName, user -> {
            registerVisitedLocation0(user, visitedLocation);
            return null;
        });
    }

    private void registerVisitedLocation0(InternalUserEntity user, @NonNull VisitedLocation visitedLocation) {
        LinkedList<InternalVisitedLocationEntity> visitedLocations = user.getVisitedLocations();
        if (visitedLocations.size() >= VISITED_LOCATION_LIMIT) {
            visitedLocations.removeFirst();
        }
        visitedLocations.addLast(internalUserMapper.fromVisitedLocation(visitedLocation));
    }

    @Override
    public boolean registerReward(String userName, UserReward reward) throws UserNotFoundException {
        return withUserLocked(userName, user -> {
            if (!user.hasRewardForAttraction(reward.getAttraction().getName())) {
                user.putReward(internalUserMapper.fromReward(reward));
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean hasRewardForAttraction(String userName, String attractionName) throws UserNotFoundException {
        return withUserLocked(userName, user -> user.hasRewardForAttraction(attractionName));
    }
}
