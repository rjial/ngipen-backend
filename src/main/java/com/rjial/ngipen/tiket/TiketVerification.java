package com.rjial.ngipen.tiket;

import jakarta.persistence.*;
import lombok.*;

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
    @NonNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tiket_tiketverifikasi", referencedColumnName = "id_tiket")
    private Tiket tiketToVerification;
}
