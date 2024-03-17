package com.rjial.ngipen.payment;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
public class CheckoutResponse {
    @JsonValue
    private List<Checkout> checkouts;
}
