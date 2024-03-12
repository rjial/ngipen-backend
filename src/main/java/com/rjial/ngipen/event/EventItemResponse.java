package com.rjial.ngipen.event;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonManagedReference
    private Event event;
}
