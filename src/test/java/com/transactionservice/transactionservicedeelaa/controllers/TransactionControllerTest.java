package com.transactionservice.transactionservicedeelaa.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transactionservice.transactionservicedeelaa.dtos.APIResponse;
import com.transactionservice.transactionservicedeelaa.dtos.Statistics;
import com.transactionservice.transactionservicedeelaa.dtos.TransactionRequest;
import com.transactionservice.transactionservicedeelaa.enums.PaymentType;
import com.transactionservice.transactionservicedeelaa.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(value="abc")
public class TransactionControllerTest {

    @MockBean
    TransactionService transactionService;

    @Autowired
    private WebApplicationContext context;

    TransactionRequest transactionRequest;

    MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        transactionRequest = TransactionRequest.builder()
                .amount("300")
                .beneficiaryAccountNumber("22327382383")
                .beneficiaryName("my name")
                .cardCVVNo(123)
                .creditCardNo("122345678987654321")
                .paymentRef("abc")
                .sortCode("1234")
                .timeStamp("2022-12-05T00:28:00Z")
                .paymentType(PaymentType.ATM_CARD)
                .destinationBankName("def")
                .build();

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(value="abcdef@gh.com", authorities = {"ROLE_MERCHANT"})
    void makePayment() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Mockito.when(transactionService.makePayment(any(TransactionRequest.class), any(String.class)))
                .thenReturn(paymentResponse());
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(transactionRequest)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @WithMockUser(value="abcdef@gh.com", authorities = {"ROLE_MERCHANT"})
    void statistics() throws Exception {
        Mockito.when(transactionService.getStatistics(any(String.class)))
                .thenReturn(getStatistics());
        mockMvc.perform(get("/api/transactions/statistics")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.sum").value(BigDecimal.valueOf(1000)))
                .andExpect(jsonPath("$.data.avg").value(240.0))
                .andExpect(jsonPath("$.data.max").value(BigDecimal.valueOf(300)))
                .andExpect(jsonPath("$.data.min").value(BigDecimal.valueOf(180)))
                .andExpect(jsonPath("$.data.count").value(5));
    }

    private APIResponse<String> paymentResponse() {
        return new APIResponse<>(true, "success", 200, null);
    }

    private APIResponse<Statistics> getStatistics() {
        Statistics statistics = new Statistics();
        statistics.setSum(BigDecimal.valueOf(1000));
        statistics.setAvg(240);
        statistics.setMax(BigDecimal.valueOf(300));
        statistics.setMin(BigDecimal.valueOf(180));
        statistics.setCount(5);
        return new APIResponse<>(true, "success", 200, statistics);
    }
}
