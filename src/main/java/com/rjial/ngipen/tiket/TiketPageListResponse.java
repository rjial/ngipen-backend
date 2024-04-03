package com.rjial.ngipen.tiket;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
public class TiketPageListResponse {
    @JsonValue
    private Page<TiketItemListResponse> tiketList;
}
