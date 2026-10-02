package com.ridelink.accountservice.service;

import com.ridelink.accountservice.dto.UpdateProfileRequest;
import com.ridelink.accountservice.dto.UserResponse;
import com.ridelink.accountservice.model.AccountStatus;
import com.ridelink.accountservice.model.Role;

public interface AccountService {

    UserResponse getCurrentUserProfile(String userId);

    UserResponse updateCurrentUserProfile(String userId, UpdateProfileRequest request);

    UserResponse getAccountById(String id);

    UserResponse updateAccountStatus(String id, AccountStatus status);

    UserResponse updateAccountRole(String id, Role role);
}
