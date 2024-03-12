package com.rjial.ngipen.event;

import com.rjial.ngipen.tiket.JenisTiket;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, Long> {

    Event findEventByUuid(UUID uuid);

    @Query(value = "select jn from JenisTiket jn join jn.event e where e.uuid = :uuid")
    List<JenisTiket> findJenisTiketByUuid(@Param("uuid") UUID uuid);
}
