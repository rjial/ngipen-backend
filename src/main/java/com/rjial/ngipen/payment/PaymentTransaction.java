package com.rjial.ngipen.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.tiket.Tiket;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payment_transaction")
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paymenttransaction")
    @JsonIgnore
    private Long id;

    @Column(name = "uuid_paymenttransaction")
    private UUID uuid;

    @Column(name = "total_paymenttransaction")
    private Integer total;

    @Column(name = "tanggal_paymenttransaction")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime date;

    @Column(name = "status_paymenttransaction")
    @Enumerated(EnumType.ORDINAL)
    @JsonSerialize(using = PaymentStatusSerializer.class)
    private PaymentStatus status;

    @OneToMany(mappedBy = "paymentTransaction")
    @JsonIgnore
    private List<PaymentHistory> paymentHistories;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id_user")
    private User user;

    @OneToOne
    @JoinColumn(name = "id_paymentgateway")
    private PaymentGatewayInformation paymentGatewayInformation;

    @OneToMany(mappedBy = "paymentTransaction")
    private List<Tiket> tikets;

}
