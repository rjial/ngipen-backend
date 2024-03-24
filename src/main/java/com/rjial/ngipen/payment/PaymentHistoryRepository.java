package com.rjial.ngipen.payment;

import com.rjial.ngipen.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

    @Query(value = "select pyhs from PaymentHistory pyhs join pyhs.user us where us = :user")
    List<PaymentHistory> findAllByUser(@Param("user")User user);
}
