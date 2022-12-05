package com.transactionservice.transactionservicedeelaa.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="accounts")
@SuperBuilder
@JsonDeserialize(builder = Account.AccountBuilder.class)
@Data
@NoArgsConstructor
public class Account {

    private static final long serialVersionUID = 8387016635308482482L;

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid2")
    @Column(length = 60)
    private String accountId;

    @OneToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private UserEntity user;

    @Column(length = 100)
    private String description;

    @Column(length = 100)
    private LocalDateTime createdDate;

    @Column(length = 100)
    private BigDecimal amount;
}
