package com.transactionservice.transactionservicedeelaa.security;

import com.transactionservice.transactionservicedeelaa.dtos.APIResponse;
import com.transactionservice.transactionservicedeelaa.dtos.LoginRequest;
import com.transactionservice.transactionservicedeelaa.dtos.LoginResponse;
import com.transactionservice.transactionservicedeelaa.entities.UserEntity;
import com.transactionservice.transactionservicedeelaa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;

import static com.transactionservice.transactionservicedeelaa.enums.UserRole.MERCHANT;
import static com.transactionservice.transactionservicedeelaa.enums.UserRole.USER;

@Service
public class AuthenticateService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public AuthenticateService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email);
        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        Collection<? extends GrantedAuthority> authorities = user.getUserRole().equals(MERCHANT)?
                MERCHANT.getGrantedAuthorities() : USER.getGrantedAuthorities();
        return new SecurityUser(email, user.getEncryptedPassword(), authorities);
    }

    public APIResponse<LoginResponse> loginResponse(LoginRequest loginRequest, AuthenticationManager authenticationManager, JwtService jwtService) {
        String email = loginRequest.getEmail();
        if (email == null || "".equals(email.trim())) {
            return new APIResponse<>(false, "Email can not be empty", 400, new LoginResponse());
        }
        email = email.toLowerCase();

        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, loginRequest.getPassword()));
        if (!auth.isAuthenticated()) {
            return new APIResponse<>(false, "Authentication failed", 403, new LoginResponse());
        }

        UserEntity user = userRepository.findByEmail(email);

        String result = "Bearer " + jwtService.generateToken(new SecurityUser(email, loginRequest.getPassword(), auth.getAuthorities()));

        LoginResponse response = LoginResponse.builder()
                .id(user.getId())
                .token(result)
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getUserRole())
                .phoneNumber(user.getPhoneNumber())
                .loginDate(LocalDateTime.now())
                .companyName(user.getCompanyName())
                .build();

        return new APIResponse<>(true, "success", 200, response);
    }

}

