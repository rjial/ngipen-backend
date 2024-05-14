package com.rjial.ngipen.tiket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TiketUserItemListResponse {
    private UUID uuid;
    private String namaUser;
}
