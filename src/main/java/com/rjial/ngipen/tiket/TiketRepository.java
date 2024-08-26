package com.rjial.ngipen.tiket;

import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.event.Event;
import com.rjial.ngipen.payment.PaymentTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TiketRepository extends JpaRepository<Tiket, Long> {

    List<Tiket> findAllByUser(User user);
    @Query("SELECT t FROM Tiket t JOIN t.paymentTransaction pt JOIN t.user u WHERE u = :user ORDER BY pt.date DESC")
    Page<Tiket> findAllByUser(@Param("user") User user, Pageable pageable);
    Optional<Tiket> findByUuid(UUID uuid);
    List<Tiket> findAllByPaymentTransaction(PaymentTransaction paymentTransaction);
    @Query("SELECT t FROM Tiket t JOIN t.jenisTiket jt JOIN jt.event e WHERE e = :event")
    Page<Tiket> findTiketByPemegangAcara(@Param("event") Event event, Pageable pageable);
    @Query("SELECT t FROM Tiket t JOIN t.jenisTiket jt JOIN jt.event e WHERE e = :event")
    Page<Tiket> findTiketByAdmin(@Param("event") Event event, Pageable pageable);
    @Query("SELECT t FROM Tiket t JOIN t.jenisTiket jt JOIN jt.event e JOIN t.paymentTransaction pt WHERE pt.id = :idPt AND e.id = :idEvent")
    Page<Tiket> findTiketByEventAndPaymentTransaction(@Param("idPt") long idPt, @Param("idEvent") long idEvent, Pageable pageable);
}
