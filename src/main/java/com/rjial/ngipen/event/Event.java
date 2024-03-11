package com.rjial.ngipen.event;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_event")
    private Long id;
    @Column(name = "nama_event")
    private String name;
    @Column(name = "uid_event")
    private UUID uuid;
    @Column(name = "tanggalawal_event")
    private LocalDate tanggalAwal;
    @Column(name = "waktuawal_event")
    private LocalTime waktuAwal;
    @Column(name = "waktuakhir_event")
    private LocalTime waktuAkhir;
    @Column(name = "lokasi_event")
    private String lokasi;
    @Column(name = "deskripsi_event")
    private String desc;
    @Column(name = "persen_event")
    private Long persen; 

}
