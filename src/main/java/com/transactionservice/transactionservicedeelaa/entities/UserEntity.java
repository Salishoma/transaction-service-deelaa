package com.transactionservice.transactionservicedeelaa.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.transactionservice.transactionservicedeelaa.enums.BusinessType;
import com.transactionservice.transactionservicedeelaa.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@SuperBuilder
@JsonDeserialize(builder = UserEntity.UserEntityBuilder.class)
@NoArgsConstructor
@Table(name="users")
public class UserEntity {

    private static final long serialVersionUID = 8388016639308402482L;

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid2")
    @Column(length = 60)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String companyName;

    @Column(nullable = false, length = 100)
    private String address;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable=false, unique=true, length = 100)
    private String encryptedPassword;

    private String phoneNumber;

    @OneToOne(mappedBy = "user", cascade=CascadeType.ALL)
    private Account account;

    @OneToMany(mappedBy = "user")
    private List<Transaction> transactions;

    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private LocalDateTime createdDate;

}
