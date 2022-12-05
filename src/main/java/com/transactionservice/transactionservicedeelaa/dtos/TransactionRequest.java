package com.transactionservice.transactionservicedeelaa.dtos;

import com.transactionservice.transactionservicedeelaa.enums.PaymentType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {
    private String amount;
    private String timeStamp;
    private PaymentType paymentType;
    private String beneficiaryAccountNumber;
    private String destinationBankName;
    private String paymentRef;
    private String beneficiaryName;
    private String sortCode;
    private String creditCardNo;
    private int cardCVVNo;
    private int cardExpMonth;
    private int cardExpYear;
}
