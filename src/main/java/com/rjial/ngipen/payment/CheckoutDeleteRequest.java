package com.rjial.ngipen.payment;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@Builder
public class CheckoutDeleteRequest {
    private String uuid;
}
