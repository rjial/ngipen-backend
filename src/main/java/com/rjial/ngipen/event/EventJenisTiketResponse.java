package com.rjial.ngipen.event;

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
    private List<JenisTiket> jenisTikets;
}
