package com.rjial.ngipen.event;

import com.rjial.ngipen.common.DataResponse;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class EventItemResponse implements DataResponse {
    private Event event;
}
