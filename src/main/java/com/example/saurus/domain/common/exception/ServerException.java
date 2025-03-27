package com.example.saurus.domain.common.exception;

public class ServerException extends RuntimeException {

    public ServerException(String message) {
        super(message);
    }
}