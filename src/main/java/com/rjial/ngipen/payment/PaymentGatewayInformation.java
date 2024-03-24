package com.rjial.ngipen.payment;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payment_gateway_info")
public class PaymentGatewayInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paymentgatewayinfo")
    private Long id;

    @Column(name = "uuid_paymentgatewayinfo")
    private UUID uuid = UUID.randomUUID();

    @Column(name = "transaction_type_paymentgatewayinfo")
    private String transactionType;
    @Column(name = "transaction_time_paymentgatewayinfo")
    private String transactionTime;
    @Column(name = "transaction_status_paymentgatewayinfo")
    private String transactionStatus;
    @Column(name = "transaction_id_paymentgatewayinfo")
    private String transactionId;
    @Column(name = "status_message_paymentgatewayinfo")
    private String statusMessage;
    @Column(name = "status_code_paymentgatewayinfo")
    private String statusCode;
    @Column(name = "signature_key_paymentgatewayinfo")
    private String signatureKey;
    @Column(name = "reference_id_paymentgatewayinfo")
    private String referenceId;
    @Column(name = "payment_type_paymentgatewayinfo")
    private String paymentType;
    @Column(name = "order_id_paymentgatewayinfo")
    private String orderId;
    @Column(name = "merchant_id_paymentgatewayinfo")
    private String merchantId;
    @Column(name = "gross_amount_paymentgatewayinfo")
    private String grossAmount;
    @Column(name = "fraud_status_paymentgatewayinfo")
    private String fraudStatus;
    @Column(name = "expiry_time_paymentgatewayinfo")
    private String expiryTime;
    @Column(name = "currency_paymentgatewayinfo")
    private String currency;
    @Column(name = "acquirer_paymentgatewayinfo")
    private String acquirer;

    @OneToOne(mappedBy = "paymentGatewayInformation")
    private PaymentTransaction paymentTransaction;
}
