package com.rjial.ngipen.tiket;

import java.util.UUID;

import com.rjial.ngipen.auth.User;

import com.rjial.ngipen.payment.PaymentTransaction;
import io.micrometer.common.lang.NonNull;
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
@Table(name = "tiket")
public class Tiket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_tiket")
    private Long id;
    @NonNull
    @Column(name = "uid_tiket")
    private UUID uuid;
    @NonNull
    @Column(name = "status_tiket")
    private Boolean statusTiket;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id_user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_jenistiket", referencedColumnName = "id_jenistiket")
    private JenisTiket jenisTiket;

    @ManyToOne
    @JoinColumn(name = "id_paymenttransaction", referencedColumnName = "id_paymenttransaction")
    private PaymentTransaction paymentTransaction;
}
