package com.rjial.ngipen.tiket;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "transaksi_tiket")
public class TransaksiTiket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaksitiket")
    private Long id;
    @Column(name = "totalharga_transaksitiket")
    private Long totalHarga;
    @Column(name = "status_transaksitiket")
    private String status;
    @Column(name = "tanggaltransaksi_transaksitiket")
    private Date tanggalTransaksi;

    @OneToOne(mappedBy = "transaksiTiket")
    private Tiket tiket;
}
