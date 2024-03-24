package com.rjial.ngipen.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
public class PaymentGatewayNotificationResponse {
    @JsonProperty("transaction_type")
    private String transactionType;
    @JsonProperty("transaction_time")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime transactionTime;
    @JsonProperty("transaction_status")
    private String transactionStatus;
    @JsonProperty("transaction_id")
    private String transactionId;
    @JsonProperty("status_message")
    private String statusMessage;
    @JsonProperty("status_code")
    private String statusCode;
    @JsonProperty("signature_key")
    private String signatureKey;
    @JsonProperty("reference_id")
    private String referenceId;
    @JsonProperty("payment_type")
    private String paymentType;
    @JsonProperty("order_id")
    private String orderId;
    @JsonProperty("merchant_id")
    private String merchantId;
    @JsonProperty("gross_amount")
    private String grossAmount;
    @JsonProperty("fraud_status")
    private String fraudStatus;
    @JsonProperty("expiry_time")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime expiryTime;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("acquirer")
    private String acquirer;

}
