package com.rjial.ngipen.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateJenisTiketRequest {
    private String name;
    private Long harga;
}
