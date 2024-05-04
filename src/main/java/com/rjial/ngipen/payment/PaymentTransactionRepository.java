package com.rjial.ngipen.payment;

import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.event.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        @Query("SELECT pt FROM PaymentTransaction pt JOIN pt.paymentHistories ph JOIN ph.event e JOIN e.pemegangEvent pe WHERE pe.id = :idUser")
        Page<PaymentTransaction> findPaymentTransactionByPemegangEvent(@Param("idUser") long idUser, Pageable pageable);
        @Query("SELECT pt FROM PaymentTransaction pt INNER JOIN pt.paymentHistories ph INNER JOIN ph.event e WHERE e.id = :idEvent")
        Page<PaymentTransaction> findPaymentTransactionByEvent(@Param("idEvent") long idEvent, Pageable pageable);
}
