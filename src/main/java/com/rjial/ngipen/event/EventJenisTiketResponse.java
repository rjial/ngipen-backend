package com.rjial.ngipen.event;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import com.rjial.ngipen.tiket.JenisTiket;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
public class EventJenisTiketResponse {
    @JsonValue
    private List<JenisTiket> jenisTikets;
}
