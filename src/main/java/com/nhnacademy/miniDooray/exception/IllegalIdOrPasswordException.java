package com.nhnacademy.miniDooray.exception;

public class IllegalIdOrPasswordException extends RuntimeException {
    public IllegalIdOrPasswordException(String message) {
        super(message);
    }
}
