package com.rjial.ngipen.payment;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@NoArgsConstructor
public class OrderItemRequest {
    private Integer total;
    private Long jenisTiket;

}
