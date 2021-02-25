package com.tourguide.users.internaluser.entity;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString(of = {"id", "name"})
@EqualsAndHashCode
public class InternalUserEntity {
    private final UUID id;
    private final String name;
    private @Setter String phoneNumber;
    private @Setter String emailAddress;
    private final LinkedList<InternalVisitedLocationEntity> visitedLocations = new LinkedList<>();
    private final InternalUserPreferencesEntity preferences = new InternalUserPreferencesEntity();
    private final Map<String, InternalUserRewardEntity> rewards = new LinkedHashMap<>();

    public InternalUserEntity(UUID id, String name, String phoneNumber, String emailAddress) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }

    public ZonedDateTime getLatestLocationTimestamp() {
        return visitedLocations.isEmpty() ? null : visitedLocations.getLast().getTimeVisited();
    }

    public InternalVisitedLocationEntity getLatestLocation() {
        return visitedLocations.isEmpty() ? null : visitedLocations.getLast();
    }

    public Collection<InternalUserRewardEntity> getRewards() {
        return rewards.values();
    }

    public boolean hasRewardForAttraction(String attractionName) {
        return rewards.containsKey(attractionName);
    }

    public void putReward(InternalUserRewardEntity reward) {
        rewards.put(reward.getAttraction().getName(), reward);
    }
}