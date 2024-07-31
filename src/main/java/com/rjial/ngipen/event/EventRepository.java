            package com.rjial.ngipen.event;

            import com.rjial.ngipen.tiket.JenisTiket;
            import org.springframework.data.domain.Page;
            import org.springframework.data.domain.Pageable;
            import org.springframework.data.jpa.repository.JpaRepository;
            import org.springframework.data.jpa.repository.Query;
            import org.springframework.data.repository.query.Param;

            import java.util.List;
            import java.util.UUID;

            public interface EventRepository extends JpaRepository<Event, Long> {

                Event findEventByUuid(UUID uuid);

                @Query(value = "select jn from JenisTiket jn join jn.event e where e.uuid = :uuid")
                List<JenisTiket> findJenisTiketByUuid(@Param("uuid") UUID uuid);

                @Query("SELECT e from Event e JOIN e.pemegangEvent pe WHERE pe.id = :idPE")
                Page<Event>  findAllByPemegangEvent(@Param("idPE") long idPe, Pageable pageable);
                @Query("SELECT e FROM Event e WHERE e.verifyEvent = true ORDER BY e.tanggalAwal DESC ")
                Page<Event>  findAllByVerifiedStatus(Pageable pageable);
            }
