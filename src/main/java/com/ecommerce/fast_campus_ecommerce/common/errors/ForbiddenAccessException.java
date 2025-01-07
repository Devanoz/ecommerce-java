package com.ecommerce.fast_campus_ecommerce.common.errors;

public class ForbiddenAccessException extends RuntimeException {
    public ForbiddenAccessException(String message) {
        super(message);
    }
}
