package com.rjial.ngipen.payment;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@Builder
public class CheckoutUpdateRequest {
    private int total;
    private String uuid;
}
