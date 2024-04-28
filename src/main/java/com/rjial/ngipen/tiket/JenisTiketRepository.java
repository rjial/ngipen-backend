package com.rjial.ngipen.tiket;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JenisTiketRepository extends JpaRepository<JenisTiket, Long> {
    @Override
    Optional<JenisTiket> findById(Long aLong);
}
