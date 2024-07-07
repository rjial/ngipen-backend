package com.rjial.ngipen.common;

public class InvalidFileTypeException extends RuntimeException {
    private final String message;
    public InvalidFileTypeException(String message) {
        super(message);
        this.message = message;
    }
}
