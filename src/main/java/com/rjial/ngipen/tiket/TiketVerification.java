package com.rjial.ngipen.tiket;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TiketVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NonNull
    @Column(name = "uuid_tiketverifikasi", unique = true, nullable = false)
    private UUID uuid;
    @Column(name = "barcodetext_tiketverifikasi", unique = true, nullable = false)
    private String barcodetext;
    @Column(name = "verificationdate_tiketverifikasi")
    private LocalDateTime verificationDateTime;
    @NonNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tiket_tiketverifikasi", referencedColumnName = "id_tiket")
    private Tiket tiketToVerification;
}
