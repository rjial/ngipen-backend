package com.rjial.ngipen.tiket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TiketVerificationRepository extends JpaRepository<TiketVerification, Long> {
    Optional<TiketVerification> findByUuid(UUID uuid);
    @Query("SELECT tv FROM TiketVerification tv WHERE tv.barcodetext = :barcodeText")
    Optional<TiketVerification> findByBarcodeText(@Param("barcodeText") String barcodeText);
}
