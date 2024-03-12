package com.rjial.ngipen.event;

import com.fasterxml.jackson.annotation.*;
import com.rjial.ngipen.common.DataResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
public class ListEventResponse implements DataResponse {
    @JsonValue
    private List<Event> eventList;
}
