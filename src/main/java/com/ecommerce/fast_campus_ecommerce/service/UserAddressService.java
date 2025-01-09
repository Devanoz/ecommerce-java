package com.ecommerce.fast_campus_ecommerce.service;

import com.ecommerce.fast_campus_ecommerce.model.UserAddressRequest;
import com.ecommerce.fast_campus_ecommerce.model.UserAddressResponse;

import java.util.List;

public interface UserAddressService {
    UserAddressResponse createAddress(Long userId, UserAddressRequest request);

    List<UserAddressResponse> findByUserId(Long userId);

    UserAddressResponse findById(Long id);

    UserAddressResponse update(Long addressId, UserAddressRequest request);

    void delete(Long addressId);

    UserAddressResponse setDefaultAddress(Long userId, Long addressId);


}
