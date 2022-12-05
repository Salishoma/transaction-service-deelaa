package com.transactionservice.transactionservicedeelaa.repository;

import com.transactionservice.transactionservicedeelaa.dtos.Statistics;
import com.transactionservice.transactionservicedeelaa.entities.Transaction;
import com.transactionservice.transactionservicedeelaa.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    @Query("select new com.transactionservice.transactionservicedeelaa.dtos.Statistics(sum(t.amount), avg(t.amount), max(t.amount), " +
            "min(t.amount), count(t)) from Transaction t where "
            + "t.user.email=:email")
    Statistics findTransactionStatisticsByUser(String email);

    @Modifying
    @Query("delete from Transaction t where t.user=:user")
    void deleteTransactionsByEmail(UserEntity user);
}
