package com.rjial.ngipen.tiket;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rjial.ngipen.auth.User;

import com.rjial.ngipen.auth.UserSerializer;
import com.rjial.ngipen.payment.PaymentHistory;
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
    @JsonSerialize(using = UserSerializer.class)
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_jenistiket", referencedColumnName = "id_jenistiket")
    private JenisTiket jenisTiket;

    @ManyToOne
    @JoinColumn(name = "id_paymenttransaction", referencedColumnName = "id_paymenttransaction")
    @JsonIgnore
    private PaymentTransaction paymentTransaction;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_paymenthistory", referencedColumnName = "id_paymenthistory")
    @JsonIgnore
    private PaymentHistory paymentHistory;

    @OneToOne(mappedBy = "tiketToVerification")
    @JsonIgnore
    private TiketVerification tiketVerification;
}
