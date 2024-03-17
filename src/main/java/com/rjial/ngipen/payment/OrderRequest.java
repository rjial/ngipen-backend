package com.rjial.ngipen.payment;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {

    private List<OrderItemRequest> orders;
}
