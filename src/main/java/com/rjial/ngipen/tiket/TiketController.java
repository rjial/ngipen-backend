package com.rjial.ngipen.tiket;

import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.common.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/verify")
    public ResponseEntity<Response<TiketVerifikasiResponse>> verifyTiket(@AuthenticationPrincipal User user, @RequestBody TiketVerifikasiRequest request) {
        TiketVerifikasiResponse verifiedTiket = tiketService.verifyTiket(request.getUuid(), user);
        Response<TiketVerifikasiResponse> tiketResponse = new Response<>();
        tiketResponse.setData(verifiedTiket);
        tiketResponse.setMessage("Tiket berhasil diverifikasi");
        tiketResponse.setStatusCode((long) HttpStatus.OK.value());
        return ResponseEntity.ok(tiketResponse);
    }
}
