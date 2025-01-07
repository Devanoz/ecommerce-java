package com.ecommerce.fast_campus_ecommerce.common.errors;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Email is already in use: " + email);
    }
}
