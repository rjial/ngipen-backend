package com.rjial.ngipen.tiket;

import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.payment.PaymentTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TiketRepository extends JpaRepository<Tiket, Long> {

    List<Tiket> findAllByUser(User user);
    Page<Tiket> findAllByUser(User user, Pageable pageable);
    Optional<Tiket> findByUuid(UUID uuid);
    List<Tiket> findAllByPaymentTransaction(PaymentTransaction paymentTransaction);
}
