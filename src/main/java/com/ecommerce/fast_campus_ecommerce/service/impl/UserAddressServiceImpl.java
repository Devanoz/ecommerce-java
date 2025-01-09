package com.ecommerce.fast_campus_ecommerce.service.impl;

import com.ecommerce.fast_campus_ecommerce.common.errors.ForbiddenAccessException;
import com.ecommerce.fast_campus_ecommerce.common.errors.ResourceNotFoundException;
import com.ecommerce.fast_campus_ecommerce.entity.UserAddress;
import com.ecommerce.fast_campus_ecommerce.model.UserAddressRequest;
import com.ecommerce.fast_campus_ecommerce.model.UserAddressResponse;
import com.ecommerce.fast_campus_ecommerce.repository.UserAddressRepository;
import com.ecommerce.fast_campus_ecommerce.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressRepository userAddressRepository;

    @Override
    public UserAddressResponse createAddress(Long userId, UserAddressRequest request) {
        UserAddress newAddress = UserAddress.builder()
                .userId(userId)
                .addressName(request.getAddressName())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .isDefault(request.isDefault())
                .build();

        if (request.isDefault()) {
            Optional<UserAddress> existingDefaultUserAddress = userAddressRepository.findByUserIdAndIsDefaultTrue(userId);
            existingDefaultUserAddress.ifPresent(userAddress -> {
                userAddress.setIsDefault(false);
                userAddressRepository.save(userAddress);
            });
        }
        UserAddress savedAddress = userAddressRepository.save(newAddress);
        return UserAddressResponse.fromUserAdress(savedAddress);
    }

    @Override
    public List<UserAddressResponse> findByUserId(Long userId) {
        List<UserAddress> addresses = userAddressRepository.findByUserId(userId);
        return addresses.stream()
                .map(UserAddressResponse::fromUserAdress)
                .toList();
    }

    @Override
    public UserAddressResponse findById(Long id) {
        UserAddress userAddress = userAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address with id " + id + "is not found"));
        return UserAddressResponse.fromUserAdress(userAddress);
    }

    @Override
    public UserAddressResponse update(Long addressId, UserAddressRequest request) {
        UserAddress existingAddress = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address with id " + addressId + " is not found"));

        UserAddress updatedAddress = UserAddress.builder()
                .userAddressId(existingAddress.getUserAddressId())
                .addressName(request.getAddressName())
                .streetAddress(request.getStreetAddress())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .isDefault(request.isDefault())
                .build();

        if (request.isDefault() && !existingAddress.getIsDefault()) {
            Optional<UserAddress> existingDefault = userAddressRepository.findByUserIdAndIsDefaultTrue(
                    existingAddress.getUserId());
            existingDefault.ifPresent(address -> {
                address.setIsDefault(false);
                userAddressRepository.save(address);
            });
        }
        UserAddress savedUserAddress = userAddressRepository.save(updatedAddress);
        return UserAddressResponse.fromUserAdress(savedUserAddress);
    }

    @Override
    public void delete(Long addressId) {
        UserAddress existingAdress = userAddressRepository.findById(addressId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Address with id" + addressId + " is not found")
                );
        userAddressRepository.delete(existingAdress);
        if(existingAdress.getIsDefault()) {
            List<UserAddress> remainingAddress = userAddressRepository.findByUserId(addressId);
            if(!remainingAddress.isEmpty()) {
                UserAddress newDefaultAddress = remainingAddress.getFirst();
                newDefaultAddress.setIsDefault(true);
                userAddressRepository.save(newDefaultAddress);
            }
        }
    }

    @Override
    public UserAddressResponse setDefaultAddress(Long userId, Long addressId) {
        UserAddress existingAddress = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address with id " + addressId + " is not found"));

        if (!existingAddress.getUserId().equals(userId)) {
            throw new ForbiddenAccessException("Address does not belong to this user");
        }

        Optional<UserAddress> existingDefault = userAddressRepository.findByUserIdAndIsDefaultTrue(
                existingAddress.getUserId());
        existingDefault.ifPresent(address -> {
            address.setIsDefault(false);
            userAddressRepository.save(address);
        });

        existingAddress.setIsDefault(true);
        UserAddress userAddress = userAddressRepository.save(existingAddress);
        return UserAddressResponse.fromUserAdress(userAddress);
    }
}
