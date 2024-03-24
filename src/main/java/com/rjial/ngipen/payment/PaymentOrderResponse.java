package com.rjial.ngipen.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;


@Data
@Getter
@Setter
@AllArgsConstructor
public class PaymentOrderResponse {

    @JsonProperty("payment_transaction")
    private PaymentTransaction paymentTransaction;
    @JsonProperty("snap_token")
    private String snapToken;
    @JsonProperty("client_key")
    private String clientKey;
}
