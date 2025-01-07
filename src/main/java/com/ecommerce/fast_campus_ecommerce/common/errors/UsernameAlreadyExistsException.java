package com.ecommerce.fast_campus_ecommerce.common.errors;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super("Username is already in use: " + username);
    }
}
