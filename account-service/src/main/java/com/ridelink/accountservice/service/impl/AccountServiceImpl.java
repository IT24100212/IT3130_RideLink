package com.ridelink.accountservice.service.impl;

import com.ridelink.accountservice.dto.UpdateProfileRequest;
import com.ridelink.accountservice.dto.UserResponse;
import com.ridelink.accountservice.exception.ResourceNotFoundException;
import com.ridelink.accountservice.model.AccountStatus;
import com.ridelink.accountservice.model.Role;
import com.ridelink.accountservice.model.User;
import com.ridelink.accountservice.repository.UserRepository;
import com.ridelink.accountservice.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;

    public AccountServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse getCurrentUserProfile(String userId) {
        User user = findUserById(userId);
        return UserResponse.fromUser(user);
    }

    @Override
    @Transactional
    public UserResponse updateCurrentUserProfile(String userId, UpdateProfileRequest request) {
        User user = findUserById(userId);

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setPhoneNumber(request.getPhoneNumber().trim());
        user.setUpdatedAt(Instant.now());

        User updatedUser = userRepository.save(user);
        return UserResponse.fromUser(updatedUser);
    }

    @Override
    public UserResponse getAccountById(String id) {
        User user = findUserById(id);
        return UserResponse.fromUser(user);
    }

    @Override
    @Transactional
    public UserResponse updateAccountStatus(String id, AccountStatus status) {
        User user = findUserById(id);

        user.setStatus(status);
        user.setUpdatedAt(Instant.now());

        User updatedUser = userRepository.save(user);
        return UserResponse.fromUser(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse updateAccountRole(String id, Role role) {
        User user = findUserById(id);

        user.setRole(role);
        user.setUpdatedAt(Instant.now());

        User updatedUser = userRepository.save(user);
        return UserResponse.fromUser(updatedUser);
    }

    private User findUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));
    }
}
