package com.transactionservice.transactionservicedeelaa.dtos;

import com.transactionservice.transactionservicedeelaa.enums.BusinessType;
import com.transactionservice.transactionservicedeelaa.enums.UserRole;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String name;
    private String companyName;
    private String Address;
    private String password;
    private String phoneNumber;
    private BusinessType businessType;
    private UserRole userRole;
}
