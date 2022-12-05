package com.transactionservice.transactionservicedeelaa.repository;

import com.transactionservice.transactionservicedeelaa.dtos.AccountResponse;
import com.transactionservice.transactionservicedeelaa.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    @Query("select new com.transactionservice.transactionservicedeelaa.dtos.AccountResponse(acc.accountId, acc.user.email" +
            ", acc.createdDate, acc.amount) from Account acc where " + "acc.user.email=:email")
    AccountResponse findAccountByUser(String email);
}
