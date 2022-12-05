package com.transactionservice.transactionservicedeelaa.services.impl;

import com.transactionservice.transactionservicedeelaa.dtos.APIResponse;
import com.transactionservice.transactionservicedeelaa.dtos.Statistics;
import com.transactionservice.transactionservicedeelaa.dtos.TransactionRequest;
import com.transactionservice.transactionservicedeelaa.entities.Account;
import com.transactionservice.transactionservicedeelaa.entities.PaymentDetail;
import com.transactionservice.transactionservicedeelaa.entities.Transaction;
import com.transactionservice.transactionservicedeelaa.entities.UserEntity;
import com.transactionservice.transactionservicedeelaa.enums.PaymentType;
import com.transactionservice.transactionservicedeelaa.exceptions.CustomException;
import com.transactionservice.transactionservicedeelaa.repository.AccountRepository;
import com.transactionservice.transactionservicedeelaa.repository.PaymentRepository;
import com.transactionservice.transactionservicedeelaa.repository.TransactionRepository;
import com.transactionservice.transactionservicedeelaa.repository.UserRepository;
import com.transactionservice.transactionservicedeelaa.services.TransactionService;
import com.transactionservice.transactionservicedeelaa.validators.DateValidator;
import com.transactionservice.transactionservicedeelaa.validators.JSONValidator;
import com.transactionservice.transactionservicedeelaa.validators.NumberValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public APIResponse<String> makePayment(TransactionRequest request, String email) {
        if (!JSONValidator.isValidJSON(request)) {
            return new APIResponse<>(false, "Invalid Json body", 400, null);
        }

        String num = request.getAmount();
        if (!NumberValidator.isValidNum(num)) {
            return new APIResponse<>(false, "Entry is not a number", 422, null);
        }

        String dateString = request.getTimeStamp();
        Instant instant = DateValidator.getInstantTime(dateString);
        if (instant == null) {
            return new APIResponse<>(false, "Invalid date format", 422, null);
        }

        Instant currentDate = Instant.now();

        LocalDateTime date = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        long elapsedTime = currentDate.getEpochSecond() - instant.getEpochSecond();
        if (elapsedTime < 0) {
            return new APIResponse<>(false, "Invalid transaction date", 204, null);
        }

        int statusCode = 200;
        if (elapsedTime >= 3600) {
            statusCode = 204;
        }

        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(num)).setScale(2, RoundingMode.HALF_DOWN);

        System.out.println("=======>Before user");
        UserEntity user = userRepository.findByEmail(email);
        Account account = user.getAccount();

        System.out.println("=======>After user");

        if (account == null) {
            throw new CustomException("Must have an account to perform transaction");
        }

        if (account.getAmount().compareTo(amount) < 0) {
            throw new CustomException("Unable to perform transaction due to insufficient amount");
        }

        LocalDate curLocalDate = LocalDate.ofInstant(currentDate, ZoneOffset.UTC);

        if (request.getCardExpYear() < curLocalDate.getYear()
                || (request.getCardExpYear() == curLocalDate.getYear()
                && request.getCardExpMonth() < curLocalDate.getMonthValue())) {
            throw new CustomException("The card is no longer valid");
        }

        PaymentType paymentType = request.getPaymentType();

        PaymentDetail.PaymentDetailBuilder<?, ?> paymentDetailBuilder = PaymentDetail.builder();

        if (PaymentType.CHEQUE.equals(paymentType)) {
            paymentDetailBuilder.paymentRef(request.getPaymentRef())
                    .beneficiaryName(request.getBeneficiaryName())
                    .sortCode(request.getSortCode());
        } else if (PaymentType.ATM_CARD.equals(paymentType)) {
            paymentDetailBuilder.creditCardNo(request.getCreditCardNo())
                    .cardCVVNo(request.getCardCVVNo())
                    .cardExpMonth(request.getCardExpMonth())
                    .cardExpYear(request.getCardExpYear());
        } else {
            throw new CustomException("Invalid payment Type");
        }

        PaymentDetail paymentDetail = paymentDetailBuilder.paymentId(UUID.randomUUID().toString())
                .paymentDate(date)
                .paymentType(paymentType)
                .amount(amount)
                .beneficiaryAccountNumber(request.getBeneficiaryAccountNumber())
                .customerName(user.getName())
                .destinationBankName(request.getDestinationBankName())
                .build();

        paymentDetail = paymentRepository.save(paymentDetail);

        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .amount(amount)
                .paymentDetail(paymentDetail)
                .user(user)
                .timestamp(date)
                .build();

        transactionRepository.save(transaction);

        account.setAmount(account.getAmount().subtract(amount));
        user.setAccount(account);

        List<Transaction> transactionList = user.getTransactions();
        transactionList.add(transaction);
        user.setTransactions(transactionList);
        accountRepository.save(account);

        return new APIResponse<>(true, "Successful", statusCode, null);
    }

    @Override
    public APIResponse<Statistics> getStatistics(String email) {
        Statistics statistics = transactionRepository.findTransactionStatisticsByUser(email);
        return new APIResponse<>(true, "Successful", 200, statistics);
    }

    @Override
    public APIResponse<?> deleteTransactions(String email) {
        UserEntity user = userRepository.findByEmail(email);
        transactionRepository.deleteTransactionsByEmail(user);
        return new APIResponse<>(true, "Successful", 200, null);
    }
}
