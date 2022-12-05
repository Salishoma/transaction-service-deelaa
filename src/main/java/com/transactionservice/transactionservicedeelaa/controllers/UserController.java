package com.transactionservice.transactionservicedeelaa.controllers;

import com.transactionservice.transactionservicedeelaa.dtos.*;
import com.transactionservice.transactionservicedeelaa.exceptions.CustomException;
import com.transactionservice.transactionservicedeelaa.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/users/")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("signup")
    public ResponseEntity<APIResponse<SignUpResponse>> signUp(@RequestBody SignUpRequest request) {
        try {
            APIResponse<SignUpResponse> apiResponse = userService.signUp(request);
            return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(apiResponse.getStatusCode()));
        } catch (CustomException ex) {
            return new ResponseEntity<>(new APIResponse<>(false, "Bad Request", 400, null), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("login")
    public ResponseEntity<APIResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        try {
            APIResponse<LoginResponse> apiResponse = userService.login(request);
            return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(apiResponse.getStatusCode()));
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(new APIResponse<>(false, "Bad Request", 400, null), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<UserResponse>>> findUsers() {
        try {
            APIResponse<List<UserResponse>> response = userService.findUsers();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(new APIResponse<>(false, ex.getMessage(), 500, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<APIResponse<UserResponse>> findUser(@PathVariable String userId) {
        try {
            APIResponse<UserResponse> response = userService.findUserById(userId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(new APIResponse<>(false, ex.getMessage(), 500, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update-user")
    public ResponseEntity<APIResponse<UserResponse>> updateUser(
            @RequestBody UserRequest request, Principal principal
    ) {
        String email = principal.getName();
        try {
            APIResponse<UserResponse> response = userService.updateUser(email, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(new APIResponse<>(false, ex.getMessage(), 404, null), HttpStatus.NOT_FOUND);
        }
    }
}
