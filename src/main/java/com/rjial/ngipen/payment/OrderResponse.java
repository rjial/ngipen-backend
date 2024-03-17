package com.rjial.ngipen.payment;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
public class OrderResponse {
    @JsonValue
    private List<Checkout> checkouts;
}
