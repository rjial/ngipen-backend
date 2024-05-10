package com.rjial.ngipen.payment;

import com.rjial.ngipen.tiket.Tiket;
import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PaymentTransactionByIdAndEventResponse {
    private PaymentTransaction paymentTransaction;
    private Page<Tiket> tikets;
}
