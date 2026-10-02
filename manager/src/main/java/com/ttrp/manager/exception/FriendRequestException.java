package com.ttrp.manager.exception;


import org.springframework.http.HttpStatus;

public class FriendRequestException extends RuntimeException {

    private final HttpStatus status;

    public FriendRequestException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
