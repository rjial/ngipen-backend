package com.rjial.ngipen.event;

import com.rjial.ngipen.auth.Level;
import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.common.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/event")
@Slf4j
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("")
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

    @PostMapping("")
    public ResponseEntity<Response<EventItemResponse>> insertEvent(@AuthenticationPrincipal User user, @RequestBody AddEventRequest request) throws Exception {
        return new ResponseEntity<>(eventService.insertEvent(request, user), HttpStatus.OK);
    }
}
