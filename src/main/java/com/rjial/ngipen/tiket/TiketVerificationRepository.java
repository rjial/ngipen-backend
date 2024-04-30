package com.rjial.ngipen.tiket;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TiketVerificationRepository extends JpaRepository<TiketVerification, Long> {
    Optional<TiketVerification> findByUuid(UUID uuid);
}
