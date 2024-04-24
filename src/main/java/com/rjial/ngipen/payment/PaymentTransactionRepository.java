package com.rjial.ngipen.payment;

import com.rjial.ngipen.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

//    @Query(value = "select pytr from PaymentTransaction pytr join pytr.")
//    List<PaymentTransaction> findPaymentTransactionByUser(@Param("user")User user);
        Optional<PaymentTransaction> findPaymentTransactionByUuid(UUID uuid);
        List<PaymentTransaction> findPaymentTransactionByUser(User user);
}
