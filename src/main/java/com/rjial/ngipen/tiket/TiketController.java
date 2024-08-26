package com.rjial.ngipen.tiket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.zxing.WriterException;
import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.common.Response;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.concurrent.AtomicInitializer;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@RestController
@RequestMapping("/tiket")
@Transactional
public class TiketController {

    private static final Logger log = LoggerFactory.getLogger(TiketController.class);
    @Autowired
    private TiketService tiketService;

    @Autowired
    private TiketRepository tiketRepository;

    @PersistenceContext
    private EntityManager entityManager;


    private final ConcurrentMap<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    @GetMapping("")
    public ResponseEntity<Response<TiketPageListResponse>> getAllTiket(@AuthenticationPrincipal User user, @RequestParam("page") int page, @RequestParam("size") int size) {
        Response<TiketPageListResponse> pageResponse = new Response<>();
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
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

    @GetMapping(value = "/{uuid}/barcode", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getTiketBarcode(@PathVariable("uuid") String uuid, @AuthenticationPrincipal User user) throws IOException {
        try {
            return ResponseEntity.ok(tiketService.generateBarcodeTiket(uuid, user));
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = "/{uuid}/user")
    public ResponseEntity<Response<User>> getUserFromTiket(@PathVariable("uuid") String uuid, @AuthenticationPrincipal User user) {
        Response<User> response = new Response<>();
        try {
            response.setData(tiketService.getUserFromTiket(uuid, user));
            response.setMessage("User has been returned");
            response.setStatusCode((long) HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (BadRequestException e) {
            response.setMessage("Failed fetching user: " + e.getMessage());
            response.setStatusCode((long) HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (NoSuchElementException e) {
            response.setMessage("User not found");
            response.setStatusCode((long) HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.setMessage("Failed fetching user: " + e.getMessage());
            response.setStatusCode((long) HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

//    @GetMapping(value = "/{uuid}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
//    public ResponseEntity<byte[]> generateTiketPdf(@PathVariable("uuid") String uuid) throws DocumentException {
//        return ResponseEntity.ok(tiketService.generateTiketPdf(uuid));
//    }

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

    @PostMapping("/qrscan")
    public ResponseEntity<Response<TiketItemListResponse>> scanQrTiket(@AuthenticationPrincipal User user, @RequestBody TiketVerificationRequest payload) {
        Response<TiketItemListResponse> tiketResponse = new Response<>();
        try {
            tiketResponse.setData(tiketService.qrScanTiket(payload, user));
            tiketResponse.setMessage("Tiket berhasil discan");
            tiketResponse.setStatusCode((long) HttpStatus.OK.value());
            return ResponseEntity.ok(tiketResponse);
        } catch (NoSuchElementException e) {
            tiketResponse.setMessage(e.getMessage());
            tiketResponse.setStatusCode((long) HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(tiketResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            tiketResponse.setMessage(e.getMessage());
            tiketResponse.setStatusCode((long) HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(tiketResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/verify/{uuidTiket}")
    public ResponseEntity<Response<TiketItemListResponse>> verifyTiketByUUIDTiket(@AuthenticationPrincipal User user, @PathVariable("uuidTiket") String uuid, @RequestParam("status") int status) {
        Response<TiketItemListResponse> tiketResponse = new Response<>();
        try {
            tiketResponse.setData(tiketService.verifyTiketByUUID(uuid, status, user));
            tiketResponse.setMessage("Tiket successfully verified!");
            tiketResponse.setStatusCode((long) HttpStatus.OK.value());
            return ResponseEntity.ok(tiketResponse);
        } catch (BadRequestException e) {
            tiketResponse.setMessage("Verifikasi Tiket Gagal : " + e.getMessage());
            tiketResponse.setStatusCode((long) HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(tiketResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            tiketResponse.setMessage(e.getMessage());
            tiketResponse.setStatusCode((long) HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(tiketResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/{uuidTiket}/status", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getStatusTicket(@PathVariable("uuidTiket") String uuidTiket, @AuthenticationPrincipal User user) {
        String name = "TIKET_"+ uuidTiket + "_" + user.getUuid().toString();
        log.info(name);
        SseEmitter sseEmitter = new SseEmitter(120000L);
        if (!sseEmitterMap.containsKey(name)) {
            sseEmitterMap.put(name, sseEmitter);

            ExecutorService sseMvcExecutor = Executors.newCachedThreadPool();
            if (sseEmitterMap.get(name) != null) {
                sseMvcExecutor.execute(() -> {
                    try {
                        Boolean b = tiketService.checkTiketStatus(uuidTiket, user);
                        log.info(name);
                        while (!b) {
                            b = tiketService.checkTiketStatus(uuidTiket, user);
                            Set<ResponseBodyEmitter.DataWithMediaType> event = SseEmitter.event()
                                    .data(tiketService.checkTiketStatus(uuidTiket, user) ? "true" : "false")
                                    .id(UUID.randomUUID().toString())
                                    .name("message")
                                    .build();
                            if(sseEmitterMap.get(name) != null) {
                                sseEmitterMap.get(name).send(event);
                            }
                            log.info(b.toString());
                            TimeUnit.SECONDS.sleep(3);
                        }
                        sseEmitterMap.get(name).complete();
                    } catch (NoSuchFieldException | IOException | InterruptedException e) {
                        sseEmitterMap.get(name).completeWithError(e);
                    }
                });
                sseEmitterMap.get(name).onCompletion(() -> {
                    sseMvcExecutor.shutdown();
                    sseEmitterMap.remove(name);
                    log.info(name + "COMPLETED");
                });
                sseEmitterMap.get(name).onTimeout(() -> {
                    sseMvcExecutor.shutdown();
                    sseEmitterMap.remove(name);
                    log.info(name + "TIME OUT");
                });
                sseEmitterMap.get(name).onError(throwable -> {
                    sseMvcExecutor.shutdown();
                    sseEmitterMap.remove(name);
                    log.info(name + "ERROR : " + throwable.getMessage());
                });
                return sseEmitterMap.get(name);
            }
        } else {
            if (sseEmitterMap.get(name) != null) {
                log.info(sseEmitterMap.get(name).toString());
                return sseEmitterMap.get(name);
            } else {
                throw new NoSuchElementException("No such element " + name);
            }
        }
        return sseEmitterMap.get(name);
    }

}
