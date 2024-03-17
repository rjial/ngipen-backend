package com.rjial.ngipen.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.auth.UserSerializer;
import com.rjial.ngipen.event.Event;
import com.rjial.ngipen.event.EventSerializer;
import com.rjial.ngipen.tiket.JenisTiket;
import com.rjial.ngipen.tiket.JenisTiketSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "checkout")
public class Checkout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_checkout")
    @JsonIgnore
    private Long id;
    @Column(name = "uuid_checkout")
    private UUID uuid = UUID.randomUUID();
    @Column(name = "total_checkout")
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


}
