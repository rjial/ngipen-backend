package com.rjial.ngipen.event;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class AddEventRequest {
    private String name;
    private String tanggalAwal;
    private String waktuAwal;
    private String waktuAkhir;
    private String lokasi;
    private String desc;
    private Long persen;
}
