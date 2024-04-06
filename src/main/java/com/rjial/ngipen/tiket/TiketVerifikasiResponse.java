package com.rjial.ngipen.tiket;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TiketVerifikasiResponse {
    @JsonProperty("status_verifikasi")
    private boolean statusVerifikasi;
    @JsonProperty("tiket")
    private TiketItemListResponse tiketItemListResponse;
}