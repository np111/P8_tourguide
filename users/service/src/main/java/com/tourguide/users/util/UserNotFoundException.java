package com.tourguide.users.util;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final String userName;

    public UserNotFoundException(String userName) {
        super("User does not exists: " + userName);
        this.userName = userName;
    }
}
