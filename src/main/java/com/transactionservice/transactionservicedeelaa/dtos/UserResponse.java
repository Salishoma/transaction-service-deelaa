package com.transactionservice.transactionservicedeelaa.dtos;

import com.transactionservice.transactionservicedeelaa.enums.UserRole;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String id;

    private String name;
    private String email;
    private UserRole userRole;
    private String phoneNumber;
    private String companyName;
    private String address;
}
