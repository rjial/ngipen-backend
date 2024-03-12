package com.rjial.ngipen.event;

import com.rjial.ngipen.common.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/")
    public ResponseEntity<Response<ListEventResponse>> getAllEvents() throws Exception {
        return new ResponseEntity<>(eventService.getAllEvents(), HttpStatus.OK);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Response<EventItemResponse>> getEventByUUID(@PathVariable UUID uuid) throws Exception {
        return new ResponseEntity<>(eventService.getEventByUUID(uuid), HttpStatus.OK);
    }

    @GetMapping("/{uuid}/jenistiket")
    public ResponseEntity<Response<EventJenisTiketResponse>> getEventJenisTiketByUUID(@PathVariable UUID uuid) throws Exception {
        return new ResponseEntity<>(eventService.getJenisTiketByUUID(uuid), HttpStatus.OK);
    }
}
