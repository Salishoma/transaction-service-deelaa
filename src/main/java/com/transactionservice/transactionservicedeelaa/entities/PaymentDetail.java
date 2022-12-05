package com.transactionservice.transactionservicedeelaa.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.transactionservice.transactionservicedeelaa.enums.PaymentType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="payment_details")
@SuperBuilder
@JsonDeserialize(builder = PaymentDetail.PaymentDetailBuilder.class)
@NoArgsConstructor
@Data
public class PaymentDetail {

    private static final long serialVersionUID = 8388016639318402462L;

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid2")
    @Column(length = 60)
    private String paymentId;

    @Enumerated(EnumType.STRING)
    PaymentType paymentType;

    @Column(length = 100)
    private String creditCardNo;

    @Column(length = 100)
    private String customerName;

    private int cardExpMonth;

    private int cardExpYear;

    @Column(length = 100, name = "card_cvv_no")
    private int cardCVVNo;

    private BigDecimal amount;

    @Column(length = 100)
    private String sortCode;

    @Column(length = 100)
    private String beneficiaryName;

    @Column(length = 100)
    private String beneficiaryAccountNumber;

    @Column(length = 100)
    private String paymentRef;

    @Column(length = 100)
    private String destinationBankName;

    @Column(length = 100)
    private LocalDateTime paymentDate;
}
