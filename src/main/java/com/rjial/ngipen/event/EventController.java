package com.rjial.ngipen.event;

import com.rjial.ngipen.auth.Level;
import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.common.Response;
import com.rjial.ngipen.tiket.JenisTiket;
import lombok.extern.slf4j.Slf4j;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<Response<Page<Event>>> getAllEvents(@RequestParam("page") int page, @RequestParam int size) throws Exception {
        return ResponseEntity.ok(eventService.getAllEvents(page, size));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Response<EventItemResponse>> getEventByUUID(@PathVariable UUID uuid) throws Exception {
        return new ResponseEntity<>(eventService.getEventByUUID(uuid), HttpStatus.OK);
    }

    @GetMapping("/{uuid}/jenistiket")
    public ResponseEntity<Response<EventJenisTiketResponse>> getEventJenisTiketByUUID(@PathVariable UUID uuid) throws Exception {
        return new ResponseEntity<>(eventService.getJenisTiketByUUID(uuid), HttpStatus.OK);
    }

    @GetMapping("/{uuid}/jenistiket/{id}")
    public ResponseEntity<Response<JenisTiket>> getEventJenisTiketDetail(@PathVariable UUID uuid, @PathVariable Long id) throws Exception {
        Response<JenisTiket> response = new Response<>();
        try {
            JenisTiket jenisTiketDetail = eventService.getJenisTiketDetail(uuid.toString(), id);
            response.setData(jenisTiketDetail);
            response.setMessage("Successfully retrieved Jenis Tiket");
            response.setStatusCode((long) HttpStatus.OK.value());
        } catch (Exception e) {
            throw new BadRequestException("Failed retrieved Jenis Tiket " + e.getMessage(), e);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{uuid}/jenistiket")
    public ResponseEntity<Response<JenisTiket>> insertJenisTiket(@PathVariable("uuid") String uuid, @AuthenticationPrincipal User user, @RequestBody AddJenisTiketRequest addJenisTiketRequest) throws Exception {
        Response<JenisTiket> response = new Response<>();
        try {
            JenisTiket jenisTiket = eventService.insertJenisTiket(uuid, user, addJenisTiketRequest);
            response.setData(jenisTiket);
            response.setMessage("Successfully inserted Jenis Tiket");
            response.setStatusCode((long) HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new BadRequestException("Gagal menambahkan jenis tiket : " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/{uuid}/jenistiket/{id}")
    public ResponseEntity<Response<String>> deleteJenisTiket(@PathVariable("uuid") String uuid, @PathVariable("id") Long id, @AuthenticationPrincipal User user) throws BadRequestException {
        Response<String> response = new Response<>();
        try {
            eventService.deleteJenisTiket(uuid, id, user);
            response.setData("Successfully deleted Jenis Tiket");
            response.setMessage("Successfully deleted Jenis Tiket");
            response.setStatusCode((long) HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new BadRequestException("Gagal menghapus jenis tiket : " + e.getMessage(), e);
        }
    }

    @PutMapping("/{uuid}/jenistiket/{id}")
    public ResponseEntity<Response<JenisTiket>> updateJenisTiket(@PathVariable("uuid") String uuid, @PathVariable("id") Long id, @AuthenticationPrincipal User user, @RequestBody UpdateJenisTiketRequest updateJenisTiketRequest) throws BadRequestException {
        Response<JenisTiket> response = new Response<>();
        try {
            response.setData(eventService.updateJenisTiket(uuid, user, id, updateJenisTiketRequest));
            response.setMessage("Successfully updated Jenis Tiket");
            response.setStatusCode((long) HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new BadRequestException("Gagal mengupdate jenis tiket : " + e.getMessage(), e);
        }
    }

    @PostMapping("")
    public ResponseEntity<Response<EventItemResponse>> insertEvent(@AuthenticationPrincipal User user, @RequestBody AddEventRequest request) throws Exception {
        return new ResponseEntity<>(eventService.insertEvent(request, user), HttpStatus.OK);
    }

    @GetMapping("/{uuid}/verify")
    public ResponseEntity<Response<EventItemResponse>> verifyEvent(@AuthenticationPrincipal User user, @PathVariable String uuid) throws Exception {
        Response<EventItemResponse> eventItemResponse = new Response<>();
        eventItemResponse.setData(eventService.verifyEvent(uuid, user));
        eventItemResponse.setMessage("Berhasil menverifikasi event");
        eventItemResponse.setStatusCode((long) HttpStatus.OK.value());
        return ResponseEntity.ok(eventItemResponse);
    }
}
