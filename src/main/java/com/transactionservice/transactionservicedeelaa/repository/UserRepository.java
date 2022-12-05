package com.transactionservice.transactionservicedeelaa.repository;

import com.transactionservice.transactionservicedeelaa.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String>  {
    UserEntity findByEmail(String email);

    Optional<UserEntity> findById(Long Id);

}
