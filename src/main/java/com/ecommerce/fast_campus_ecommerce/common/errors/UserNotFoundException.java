package com.ecommerce.fast_campus_ecommerce.common.errors;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
