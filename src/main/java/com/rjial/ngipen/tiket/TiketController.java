package com.rjial.ngipen.tiket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.common.Response;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/tiket")
public class TiketController {

    @Autowired
    private TiketService tiketService;

    @GetMapping("")
    public ResponseEntity<Response<TiketPageListResponse>> getAllTiket(@AuthenticationPrincipal User user, @RequestParam("page") int page, @RequestParam("size") int size) {
        Response<TiketPageListResponse> pageResponse = new Response<>();
        Pageable pageable = PageRequest.of(page, size);
        pageResponse.setData(tiketService.getAllTiket(user, pageable));
        pageResponse.setMessage("Tikets has been returned");
        pageResponse.setStatusCode((long) HttpStatus.OK.value());
        return ResponseEntity.ok(pageResponse);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Response<TiketItemListResponse>> getTiketFromUUID(@AuthenticationPrincipal User user, @PathVariable("uuid") String uuid) {
        TiketItemListResponse tiketFromUUID = tiketService.getTiketFromUUID(uuid, user);
        Response<TiketItemListResponse> tiketItemListResponse = new Response<>();
        tiketItemListResponse.setData(tiketFromUUID);
        tiketItemListResponse.setMessage("Tiket has been returned");
        tiketItemListResponse.setStatusCode((long) HttpStatus.OK.value());
        return ResponseEntity.ok(tiketItemListResponse);
    }

    @GetMapping(value = "/{uuid}/qr", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getTiketQrCode(@PathVariable("uuid") String uuid, @AuthenticationPrincipal User user) throws IOException {
        return ResponseEntity.ok(tiketService.generateQrTiket(uuid, user));
    }

    @PostMapping("/verify")
    public ResponseEntity<Response<TiketItemListResponse>> verifyTiket(@AuthenticationPrincipal User user, @RequestBody TiketVerificationRequest payload) throws BadRequestException, JsonProcessingException {
        Response<TiketItemListResponse> tiketResponse = new Response<>();
        try {
            TiketItemListResponse verifiedTiket = tiketService.verifyTiket(payload, user);
            tiketResponse.setData(verifiedTiket);
            tiketResponse.setMessage("Tiket berhasil diverifikasi");
            tiketResponse.setStatusCode((long) HttpStatus.OK.value());
            return ResponseEntity.ok(tiketResponse);
        } catch (BadRequestException e) {
            tiketResponse.setMessage(e.getMessage());
            tiketResponse.setStatusCode((long) HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(tiketResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            tiketResponse.setMessage(e.getMessage());
            tiketResponse.setStatusCode((long) HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(tiketResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
