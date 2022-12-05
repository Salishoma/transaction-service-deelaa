package com.transactionservice.transactionservicedeelaa.controllers;

import com.transactionservice.transactionservicedeelaa.dtos.APIResponse;
import com.transactionservice.transactionservicedeelaa.dtos.Statistics;
import com.transactionservice.transactionservicedeelaa.dtos.TransactionRequest;
import com.transactionservice.transactionservicedeelaa.services.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_MERCHANT')")
    public ResponseEntity<APIResponse<String>> makePayment(@RequestBody TransactionRequest request, Principal principal) {
        try {
            String email = principal.getName();
            APIResponse<String> apiResponse = transactionService.makePayment(request, email);
            return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(apiResponse.getStatusCode()));
        } catch (Exception ex) {ex.printStackTrace();
            return new ResponseEntity<>(new APIResponse<>(false, ex.getMessage(), 400, null), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("statistics")
    @PreAuthorize("hasAuthority('ROLE_MERCHANT')")
    public ResponseEntity<APIResponse<Statistics>> statistics(Principal principal) {
        try {
            String email = principal.getName();
            APIResponse<Statistics> apiResponse = transactionService.getStatistics(email);
            return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(apiResponse.getStatusCode()));
        } catch (Exception ex) {ex.printStackTrace();
            return new ResponseEntity<>(new APIResponse<>(false, ex.getMessage(), 400, null), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('ROLE_MERCHANT')")
    public ResponseEntity<APIResponse<?>> deleteTransaction(Principal principal) {
        try {
            String email = principal.getName();
            APIResponse<?> apiResponse = transactionService.deleteTransactions(email);
            return new ResponseEntity<>(apiResponse, HttpStatus.NO_CONTENT);
        } catch (Exception ex) {ex.printStackTrace();
            return new ResponseEntity<>(new APIResponse<>(false, ex.getMessage(), 400, null), HttpStatus.BAD_REQUEST);
        }
    }
}
