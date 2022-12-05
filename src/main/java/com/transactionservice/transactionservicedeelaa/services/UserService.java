package com.transactionservice.transactionservicedeelaa.services;

import com.transactionservice.transactionservicedeelaa.dtos.*;

import java.util.List;

public interface UserService {
    APIResponse<SignUpResponse> signUp(SignUpRequest request);
    APIResponse<LoginResponse> login(LoginRequest request) throws Exception;
    APIResponse<List<UserResponse>> findUsers();
    APIResponse<UserResponse> findUserById(String userId);
    APIResponse<UserResponse> updateUser(String userId, UserRequest request);
}
