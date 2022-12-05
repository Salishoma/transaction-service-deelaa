package com.transactionservice.transactionservicedeelaa.dtos;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpResponse {
    private String Id;
    private String name;
    private String email;
}
