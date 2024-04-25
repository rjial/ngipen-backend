package com.rjial.ngipen.tiket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rjial.ngipen.event.Event;
import com.rjial.ngipen.event.EventSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "jenis_tiket")
public class JenisTiket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_jenistiket")
    private Long id;
    @Column(name = "nama_jenistiket")
    private String nama;
    @Column(name = "harga_jenistiket")
    private Long harga;

    @ManyToOne
    @JoinColumn(name = "id_event", referencedColumnName = "id_event")
    @JsonSerialize(using = EventSerializer.class)
    @Schema(type = "string", example = "Utsuru 8.5")
    private Event event;
}
