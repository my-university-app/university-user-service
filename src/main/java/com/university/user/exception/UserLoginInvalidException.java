package com.university.user.exception;

public class UserLoginInvalidException extends RuntimeException {
    public UserLoginInvalidException(String message) {
        super(message);
    }
}
