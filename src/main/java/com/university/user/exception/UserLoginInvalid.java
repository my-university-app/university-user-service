package com.university.user.exception;

public class UserLoginInvalid extends RuntimeException {
    public UserLoginInvalid(String message) {
        super(message);
    }
}
