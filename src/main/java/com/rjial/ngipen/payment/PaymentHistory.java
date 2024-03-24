package com.rjial.ngipen.payment;

import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.event.Event;
import com.rjial.ngipen.tiket.JenisTiket;
import jakarta.persistence.*;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payment_history")
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paymenthistory")
    private Long id;

    @Column(name = "total_paymenthistory")
    @NonNull
    private Integer total;
    @NonNull
    @ManyToOne
    @JoinColumn(name = "id_event", referencedColumnName = "id_event")
//    @JsonSerialize(using = EventSerializer.class)
//    @Schema(type = "string", example = "Utsuru 8.5")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "id_jenistiket", referencedColumnName = "id_jenistiket")
    @NonNull
//    @JsonSerialize(using = JenisTiketSerializer.class)
//    @Schema(type = "string", example = "Subsidi")
    private JenisTiket jenisTiket;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id_user")
    @NonNull
//    @JsonSerialize(using = UserSerializer.class)
//    @Schema(type = "string", example = "Lorem Ipsum")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_paymenttransaction", referencedColumnName = "id_paymenttransaction")
    private PaymentTransaction paymentTransaction;
}
