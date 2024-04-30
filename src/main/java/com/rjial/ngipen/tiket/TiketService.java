package com.rjial.ngipen.tiket;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.common.Response;
import com.rjial.ngipen.event.Event;
import com.rjial.ngipen.event.EventRepository;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

@Slf4j
@Service
public class TiketService {

    @Autowired
    private TiketRepository tiketRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TiketVerificationRepository tiketVerificationRepository;

    public TiketService(Environment env) {
    }
//    public Page<Tiket> getAllTiket(int page, int size, User user) {
//        PageRequest pageRequest = PageRequest.of(page, size);
//        return tiketRepository.findAllByUser(user, pageRequest);
//    }
    public TiketListResponse getAllTiket(User user) {
//        PageRequest pageRequest = PageRequest.of(page, size);
        List<Tiket> allByUser = tiketRepository.findAllByUser(user);
        List<TiketItemListResponse> collect = allByUser.stream().map(item -> {
            return new TiketItemListResponse(item.getUuid(), item.getStatusTiket(), item.getUser().getName(), item.getJenisTiket().getNama(), item.getJenisTiket().getEvent().getName(), item.getJenisTiket().getEvent().getTanggalAwal(), item.getJenisTiket().getHarga(), item.getJenisTiket().getEvent().getWaktuAwal(), item.getJenisTiket().getEvent().getWaktuAkhir());
        }).toList();
        return new TiketListResponse(collect);
    }

    public TiketPageListResponse getAllTiket(User user, Pageable pageable) {
        Page<Tiket> allByUser = tiketRepository.findAllByUser(user, pageable);
        Page<TiketItemListResponse> map = allByUser.map(item -> {
            return new TiketItemListResponse(item.getUuid(), item.getStatusTiket(), item.getUser().getName(), item.getJenisTiket().getNama(), item.getJenisTiket().getEvent().getName(), item.getJenisTiket().getEvent().getTanggalAwal(), item.getJenisTiket().getHarga(), item.getJenisTiket().getEvent().getWaktuAwal(), item.getJenisTiket().getEvent().getWaktuAkhir());
        });
        return new TiketPageListResponse(map);
    }

    public byte[] generateQrTiket(String uuidTiket, User user) throws IOException {
        int imageSize = 200;
        try {
            Tiket tiket = tiketRepository.findByUuid(UUID.fromString(uuidTiket)).orElseThrow();
            if (tiket.getUser().getId().equals(user.getId())) {
                Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
                hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                hints.put(EncodeHintType.MARGIN, 0);
                BitMatrix bitMatrix = new MultiFormatWriter().encode(tiket.getTiketVerification().getUuid().toString(), BarcodeFormat.QR_CODE, imageSize, imageSize, hints);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                MatrixToImageWriter.writeToStream(bitMatrix, "jpeg", bos);
                return bos.toByteArray();
            } else {
                BufferedImage image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = image.createGraphics();
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, imageSize, imageSize);

                Font font = new Font("Arial", Font.BOLD,20);
                g.setFont(font);
                g.setColor(Color.BLACK);

                FontMetrics fm = g.getFontMetrics();
                int x = (imageSize - fm.stringWidth("Failed!")) / 2;
                int y = (imageSize - fm.getHeight()) / 2 + fm.getAscent();

                g.drawString("Failed!", x, y);
                g.dispose();
                log.error("User");

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write(image, "jpeg", bos);
                return bos.toByteArray();
//                throw new BadCredentialsException("Anda bukan pemilik dari tiket ini");
            }
        } catch (WriterException | IOException | JWTVerificationException e) {
            BufferedImage image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, imageSize, imageSize);

            Font font = new Font("Arial", Font.BOLD,20);
            g.setFont(font);
            g.setColor(Color.BLACK);

            FontMetrics fm = g.getFontMetrics();
            int x = (imageSize - fm.stringWidth("Failed!")) / 2;
            int y = (imageSize - fm.getHeight()) / 2 + fm.getAscent();

            g.drawString("Failed!", x, y);
            g.dispose();
            log.error(e.getMessage(), e);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpeg", bos);
            return bos.toByteArray();
        }
    }

    public Tiket verifyTiket(TiketVerificationRequest payload, User user) throws BadRequestException, JsonProcessingException {
        TiketVerification tiketVerification = tiketVerificationRepository.findByUuid(UUID.fromString(payload.getPayload())).orElseThrow();
        log.info(tiketVerification.toString());
        Event eventByUuid = eventRepository.findEventByUuid(tiketVerification.getTiketToVerification().getJenisTiket().getEvent().getUuid());
            if (eventByUuid != null) {
                Tiket tiket = tiketRepository.findByUuid(tiketVerification.getTiketToVerification().getUuid()).orElseThrow();
                LocalDateTime expiredDateTime = LocalDateTime.of(tiket.getJenisTiket().getEvent().getTanggalAwal(), tiket.getJenisTiket().getEvent().getWaktuAkhir());
                if (expiredDateTime.isBefore(LocalDateTime.now())) throw new  BadRequestException("Tiket sudah kadaluarsa");
                if (tiket.getStatusTiket()) throw new BadRequestException("Anda tidak bisa menverifikasi tiket yang sudah terverifikasi");
                tiket.setStatusTiket(true);
                return tiketRepository.save(tiket);
            } else {
                throw new BadRequestException("Event tidak ditemukan");
            }
    }



    public TiketItemListResponse getTiketFromUUID(String uuid, User user) {
        try {
            Tiket tiket = tiketRepository.findByUuid(UUID.fromString(uuid)).orElseThrow();
            TiketItemListResponse tiketItemListResponse = new TiketItemListResponse(tiket.getUuid(), tiket.getStatusTiket(), tiket.getUser().getName(), tiket.getJenisTiket().getNama(), tiket.getJenisTiket().getEvent().getName(), tiket.getJenisTiket().getEvent().getTanggalAwal(), tiket.getJenisTiket().getHarga(), tiket.getJenisTiket().getEvent().getWaktuAwal(), tiket.getJenisTiket().getEvent().getWaktuAkhir());
            if (!Objects.equals(tiket.getUser().getId(), user.getId())) throw new BadCredentialsException("Anda buka pemegang tiket");
            return tiketItemListResponse;
        } catch (Exception exc ){
            throw new DataIntegrityViolationException("Tiket is not found", exc);
        }
    }

//    public TiketVerifikasiResponse verifyTiket(String uuid, User user) {
//        try {
//            Tiket tiket = tiketRepository.findByUuid(UUID.fromString(uuid)).orElseThrow();
//            if (tiket.getJenisTiket().getEvent().getPemegangEvent().getId().equals(user.getId())) throw new BadCredentialsException("Anda bukan pemilik event dari tiket ini!");
//            tiket.setStatusTiket(true);
//            Tiket savedTiket = tiketRepository.save(tiket);
//            if (savedTiket.getStatusTiket()) {
//                TiketItemListResponse tiketItemListResponse = new TiketItemListResponse(tiket.getUuid(), tiket.getStatusTiket(), tiket.getUser().getName(), tiket.getJenisTiket().getNama(), tiket.getJenisTiket().getEvent().getName(), tiket.getJenisTiket().getEvent().getTanggalAwal(), tiket.getJenisTiket().getHarga(), tiket.getJenisTiket().getEvent().getWaktuAwal(), tiket.getJenisTiket().getEvent().getWaktuAkhir());
//                TiketVerifikasiResponse verifiedResponse = new TiketVerifikasiResponse();
//                verifiedResponse.setStatusVerifikasi(savedTiket.getStatusTiket());
//                verifiedResponse.setTiketItemListResponse(tiketItemListResponse);
//                return verifiedResponse;
//            } else {
//                throw new DataIntegrityViolationException("Gagal menverifikasi tiket");
//            }
//        } catch(Exception exc) {
//            throw new DataIntegrityViolationException("Gagal menverifikasi tiket", exc);
//        }
//    }
}
