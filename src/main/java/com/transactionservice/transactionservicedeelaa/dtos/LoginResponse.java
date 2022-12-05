package com.transactionservice.transactionservicedeelaa.dtos;

import com.transactionservice.transactionservicedeelaa.enums.UserRole;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private String id;
    private String token;

    private String name;
    private String email;
    private UserRole role;
    private String phoneNumber;
    private LocalDateTime loginDate;
    private String companyName;

}
