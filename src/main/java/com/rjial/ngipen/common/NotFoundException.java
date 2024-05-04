package com.rjial.ngipen.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Data tidak ditemukan")
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
