package com.rjial.ngipen.payment;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Data
@Getter
@Setter
@NoArgsConstructor
public class PaymentOrderRequest {

    private List<String> orders;
}
