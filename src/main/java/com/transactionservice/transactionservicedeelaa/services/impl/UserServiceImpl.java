package com.transactionservice.transactionservicedeelaa.services.impl;

import com.transactionservice.transactionservicedeelaa.dtos.*;
import com.transactionservice.transactionservicedeelaa.entities.UserEntity;
import com.transactionservice.transactionservicedeelaa.repository.UserRepository;
import com.transactionservice.transactionservicedeelaa.security.AuthenticateService;
import com.transactionservice.transactionservicedeelaa.security.JwtService;
import com.transactionservice.transactionservicedeelaa.services.UserService;
import com.transactionservice.transactionservicedeelaa.validators.EmailValidator;
import com.transactionservice.transactionservicedeelaa.utils.MapStructMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.transactionservice.transactionservicedeelaa.enums.BusinessType.NGO;
import static com.transactionservice.transactionservicedeelaa.enums.UserRole.USER;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthenticateService authenticateService;
    private final PasswordEncoder passwordEncoder;
    private final MapStructMapper mapper;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public APIResponse<SignUpResponse> signUp(SignUpRequest request) {
        if ("".equals(request.getEmail().trim())) {
            return new APIResponse<>(false, "Email can not be empty", 400, new SignUpResponse());
        }

        String email = request.getEmail().toLowerCase();
        if(!EmailValidator.isValid(email)){
            return new APIResponse<>(false, "Enter a valid email address", 400, new SignUpResponse());
        }
        if (Objects.nonNull(userRepository.findByEmail(email))) {
            return new APIResponse<>(false, "User already exist", 403,
                    new SignUpResponse());
        }

        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID().toString())
                .name(request.getName())
                .email(request.getEmail())
                .companyName(request.getCompanyName())
                .encryptedPassword(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .businessType(request.getBusinessType() == null ? NGO : request.getBusinessType())
                .userRole(request.getUserRole() == null ? USER : request.getUserRole())
                .build();

        userRepository.save(user);

        return new APIResponse<>(true, "success", 201,
                new ModelMapper().map(user, SignUpResponse.class));
    }

    @Override
    public APIResponse<LoginResponse> login(LoginRequest request) {
        return authenticateService.loginResponse(request, authenticationManager, jwtService);
    }

    @Override
    public APIResponse<List<UserResponse>> findUsers() {
        TypedQuery<UserResponse> query =
                entityManager.createQuery("SELECT new com.transactionservice.transactionservicedeelaa" +
                ".dtos.UserResponse(u.id, u.name, u.email, u.userRole, u.phoneNumber, u.companyName, u.address) " +
                "FROM UserEntity u", UserResponse.class);
        List<UserResponse> responseList = query.getResultList();
        return new APIResponse<>(true, "success", 200, responseList);
    }

    @Override
    public APIResponse<UserResponse> findUserById(String userId) {
        TypedQuery<UserResponse> query =
                entityManager.createQuery("SELECT new com.transactionservice.transactionservicedeelaa" +
                ".dtos.UserResponse(u.id, u.name, u.email, u.userRole, u.phoneNumber, u.companyName, u.address) " +
                "FROM UserEntity u WHERE u.id=:id OR u.email=:id", UserResponse.class);
        query.setParameter("id", userId);
        UserResponse response = query.getSingleResult();
        return new APIResponse<>(true, "success", 200, response);
    }

    private UserEntity findUser(String userId) {
        TypedQuery<UserEntity> query =
                entityManager.createQuery("SELECT u FROM UserEntity u WHERE u.id=:id OR u.email=:id", UserEntity.class);
        query.setParameter("id", userId);
        return query.getSingleResult();
    }

    @Override
    public APIResponse<UserResponse> updateUser(String userId, UserRequest request) {
        UserEntity user = findUser(userId);
        if (user == null) {
            return new APIResponse<>(false, "User does not exist", 404, null);
        }

        user = mapper.userRequestToUser(request, user);
        userRepository.save(user);
        UserResponse response = mapper.userToUserResponse(user);
        return new APIResponse<>(true, "success", 200, response);
    }

}
