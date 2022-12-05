package com.transactionservice.transactionservicedeelaa.utils;

import com.transactionservice.transactionservicedeelaa.dtos.AccountResponse;
import com.transactionservice.transactionservicedeelaa.dtos.UserRequest;
import com.transactionservice.transactionservicedeelaa.dtos.UserResponse;
import com.transactionservice.transactionservicedeelaa.entities.Account;
import com.transactionservice.transactionservicedeelaa.entities.UserEntity;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = "spring"
)
@Component
public interface MapStructMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserEntity userRequestToUser(UserRequest request, @MappingTarget UserEntity user);
    UserResponse userToUserResponse(UserEntity user);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    AccountResponse accountToAccountResponse(Account account, @MappingTarget AccountResponse accountResponse);
}
