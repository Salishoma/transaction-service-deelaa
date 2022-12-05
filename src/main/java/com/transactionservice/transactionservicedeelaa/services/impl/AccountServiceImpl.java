package com.transactionservice.transactionservicedeelaa.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.transactionservice.transactionservicedeelaa.dtos.*;
import com.transactionservice.transactionservicedeelaa.entities.Account;
import com.transactionservice.transactionservicedeelaa.entities.UserEntity;
import com.transactionservice.transactionservicedeelaa.exceptions.CustomException;
import com.transactionservice.transactionservicedeelaa.repository.AccountRepository;
import com.transactionservice.transactionservicedeelaa.repository.PaymentTrackerRepository;
import com.transactionservice.transactionservicedeelaa.repository.UserRepository;
import com.transactionservice.transactionservicedeelaa.services.AccountService;
import com.transactionservice.transactionservicedeelaa.utils.MapStructMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PaymentTrackerRepository paymentTrackerRepository;
    private final MapStructMapper mapper;


    @Value("${secret.key}")
    private String PAY_STACK_SECRET_KEY;

    @Value("${paystack.url}")
    private String PAY_STACK_BASE_URL;

    @Value("${paystack.verification.url}")
    private String PAY_STACK_VERIFY_URL;

    private AccountResponse createNewAccount(String email, AccountRequest accountRequest) {
        UserEntity user = userRepository.findByEmail(email);

        if (user.getAccount() != null) {
            return new AccountResponse();
        }

        BigDecimal amount = accountRequest.getAmount();
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }

        Account account = Account.builder()
                .accountId(UUID.randomUUID().toString())
                .user(user)
                .createdDate(LocalDateTime.now())
                .description(accountRequest.getDescription())
                .amount(amount)
                .build();

        account = accountRepository.save(account);
        user.setAccount(account);
        userRepository.save(user);

        return mapper.accountToAccountResponse(account, new AccountResponse());
    }

    public APIResponse<AccountResponse> createAccount(String email, AccountRequest accountRequest) {
        AccountResponse response = createNewAccount(email, accountRequest);
        if (response.getEmail() == null) {
            return new APIResponse<>(false, "You already have an account", 403, null);
        }
        return new APIResponse<>(true, "success", 201, response);
    }

    @Override
    public APIResponse<AccountResponse> findAccountByEmail(String email) {
        AccountResponse response = accountRepository.findAccountByUser(email);
        return new APIResponse<>(true, "success", 200, response);
    }

    @Override
    public APIResponse<List<String>> getPaymentTrackerKeys(String email) {
        List<String> references = paymentTrackerRepository.getPaymentTrackerKeys(email);
        return new APIResponse<>(true, "success", 200, references);
    }

    @Override
    public APIResponse<PayStackResponse> initTransaction(String email, PayStackPaymentRequest request) throws Exception {
        PayStackResponse initializeTransactionResponse;

        if (request.getAmount() <= 0) {
            throw new CustomException("Deposit must be greater than zero");
        }

        request.setEmail(email);
        try {
            Gson gson = new Gson();
            StringEntity postingString = new StringEntity(gson.toJson(request));
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(PAY_STACK_BASE_URL);
            post.setEntity(postingString);
            post.addHeader("Content-Type", "application/json");
            post.addHeader("Authorization", "Bearer " + PAY_STACK_SECRET_KEY);
            StringBuilder result = new StringBuilder();
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }

            } else {
                throw new AuthenticationException("Error Occurred while initializing transaction");
            }
            ObjectMapper mapper = new ObjectMapper();

            initializeTransactionResponse = mapper.readValue(result.toString(), PayStackResponse.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Failure initializing payStack transaction");
        }

        String reference = initializeTransactionResponse.getData().getReference();
        paymentTrackerRepository.save(reference, email);

        return new APIResponse<>(true, "success", 200, initializeTransactionResponse);
    }

    @Override
    public APIResponse<VerifyTransactionResponse> verifyPayment(String emailFromPrincipal, String reference) {
        String email = paymentTrackerRepository.findEmailByReference(emailFromPrincipal, reference);

        if (email == null) {
            throw new CustomException("Invalid payment reference");
        }

        VerifyTransactionResponse payStackResponse;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(PAY_STACK_VERIFY_URL + reference);
            request.addHeader("Content-type", "application/json");
            request.addHeader("Authorization", "Bearer " + PAY_STACK_SECRET_KEY);
            StringBuilder result = new StringBuilder();
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }

            } else {
                throw new CustomException("Error Occurred while connecting to PayStack URL");
            }
            ObjectMapper mapper = new ObjectMapper();

            payStackResponse = mapper.readValue(result.toString(), VerifyTransactionResponse.class);

            deposit(payStackResponse, email);

            paymentTrackerRepository.deleteEmailByReference(email, reference);

        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
        return new APIResponse<>(true, "success", 200, payStackResponse);
    }

    private void deposit(VerifyTransactionResponse response, String email) {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User does not exists");
        }
        Account account = user.getAccount();

        BigDecimal amount = response.getData().getAmount();
        if (account == null) {
            AccountResponse accountResponse = createNewAccount(email, new AccountRequest("Account created due to deposit", amount));
            account = accountRepository.findById(accountResponse.getAccountId()).orElse(new Account());
        }

        account.setAmount(account.getAmount().add(amount));
        account = accountRepository.save(account);

        user.setAccount(account);
        userRepository.save(user);
    }

}
