package com.rjial.ngipen.tiket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rjial.ngipen.event.Event;
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
    @ManyToOne
    @JoinColumn(name = "id_event", referencedColumnName = "id_event")
    @JsonIgnore
    private Event event;
}
