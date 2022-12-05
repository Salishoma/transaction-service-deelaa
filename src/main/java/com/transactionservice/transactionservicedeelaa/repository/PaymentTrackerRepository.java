package com.transactionservice.transactionservicedeelaa.repository;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class PaymentTrackerRepository {

    private final RedisTemplate<String, String> template;

    public void save(String reference, String email) {
        template.opsForHash().put(email, reference, email);
    }

    public String findEmailByReference(String email, String reference) {
        return (String) template.opsForHash().get(email, reference);
    }

    public List<String> getPaymentTrackerKeys(String email) {
        return template.opsForHash().keys(email)
                .stream()
                .map(obj -> (String) obj)
                .collect(Collectors.toList());
    }

    public void deleteEmailByReference(String email, String reference) {
        template.opsForHash().delete(email, reference);
    }
}
