package com.rjial.ngipen.auth;

public enum Level {
    ADMIN,
    PEMEGANG_ACARA,
    USER;


    @Override
    public String toString() {
        return switch (this) {
            case USER -> "USER";
            case ADMIN -> "ADMIN";
            case PEMEGANG_ACARA -> "PEMEGANG_ACARA";
            default -> "USER";
        };
    }
}
