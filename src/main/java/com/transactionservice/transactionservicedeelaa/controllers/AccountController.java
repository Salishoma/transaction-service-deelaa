package com.transactionservice.transactionservicedeelaa.controllers;

import com.transactionservice.transactionservicedeelaa.dtos.*;
import com.transactionservice.transactionservicedeelaa.services.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/accounts/")
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @RequestMapping(value = "", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<APIResponse<AccountResponse>> createAccount(@RequestBody AccountRequest accountRequest, Principal principal) {
        try {
            String email = principal.getName();
            APIResponse<AccountResponse> apiResponse = accountService.createAccount(email, accountRequest);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new APIResponse<>(false, ex.getMessage(), 403, null), HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(value = "fetch-account", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<APIResponse<AccountResponse>> getAccount(Principal principal) {
        try {
            String email = principal.getName();
            APIResponse<AccountResponse> apiResponse = accountService.findAccountByEmail(email);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new APIResponse<>(false, ex.getMessage(), 400, null), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "payment-trackers", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<APIResponse<List<String>>> getPaymentTrackerReferences(Principal principal) {
        try {
            String email = principal.getName();
            APIResponse<List<String>> references = accountService.getPaymentTrackerKeys(email);
            return new ResponseEntity<>(references, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new APIResponse<>(false, ex.getMessage(), 400, null), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "init-payment", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('ROLE_MERCHANT')")
    public ResponseEntity<APIResponse<PayStackResponse>> deposit(@RequestBody PayStackPaymentRequest paymentRequest, Principal principal) {
        try {
            String email = principal.getName();
            APIResponse<PayStackResponse> apiResponse = accountService.initTransaction(email, paymentRequest);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new APIResponse<>(false, ex.getMessage(), 400, null), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "verify-payment", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('ROLE_MERCHANT')")
    public ResponseEntity<APIResponse<VerifyTransactionResponse>> confirmPayment(
            @RequestBody PaystackVerifyRequestDTO paystackVerifyRequestDTO,
            Principal principal
    ) {
        try {
            String paymentRef = paystackVerifyRequestDTO.getVerificationCode();
            String email = principal.getName();
            APIResponse<VerifyTransactionResponse> apiResponse = accountService.verifyPayment(email, paymentRef);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(
                    new APIResponse<>(false, ex.getMessage(), 400, null),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
