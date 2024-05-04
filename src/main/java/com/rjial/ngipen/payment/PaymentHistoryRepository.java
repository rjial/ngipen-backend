package com.rjial.ngipen.payment;

import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.event.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

    @Query(value = "select pyhs from PaymentHistory pyhs join pyhs.user us where us = :user")
    List<PaymentHistory> findAllByUser(@Param("user")User user);
//    @Query("")
    Page<PaymentHistory> findAllByEvent(Event event, Pageable pageable);
}
