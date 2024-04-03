package com.rjial.ngipen.tiket;

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
public class TiketListResponse {
    @JsonValue
    private List<TiketItemListResponse> tiketList;
}
