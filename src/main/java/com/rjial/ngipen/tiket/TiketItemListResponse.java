package com.rjial.ngipen.tiket;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.Column;
import lombok.*;

import java.util.UUID;

@Data
@Getter
@Setter
@AllArgsConstructor
public class TiketItemListResponse {
    private UUID uuid;
    private Boolean statusTiket;
    private String namaUser;
    private String jenisTiket;
}
