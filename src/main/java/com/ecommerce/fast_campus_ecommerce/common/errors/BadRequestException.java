package com.ecommerce.fast_campus_ecommerce.common.errors;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}