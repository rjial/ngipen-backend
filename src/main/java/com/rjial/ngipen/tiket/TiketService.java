package com.rjial.ngipen.tiket;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
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
import com.rjial.ngipen.auth.Level;
import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.common.Response;
import com.rjial.ngipen.event.Event;
import com.rjial.ngipen.event.EventRepository;
import com.rjial.ngipen.payment.PaymentStatus;
import com.rjial.ngipen.payment.PaymentTransaction;
import com.rjial.ngipen.payment.PaymentTransactionRepository;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.spring6.SpringTemplateEngine;

import javax.crypto.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class TiketService {

    @Autowired
    private TiketRepository tiketRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TiketVerificationRepository tiketVerificationRepository;

    private Algorithm algorithm;
    private JWTVerifier jwtVerifier;
    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private final Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private SpringTemplateEngine springTemplateEngine;

    @Autowired
    private ServletContext servletContext;


    public TiketService(Environment env) {
        String tiketKey = env.getProperty("tiket.key");
        assert tiketKey != null;
        algorithm = Algorithm.HMAC256(tiketKey);
        jwtVerifier = JWT.require(algorithm).withIssuer("Ngipen").build();
    }
//    public Page<Tiket> getAllTiket(int page, int size, User user) {
//        PageRequest pageRequest = PageRequest.of(page, size);
//        return tiketRepository.findAllByUser(user, pageRequest);
//    }
    public TiketListResponse getAllTiket(User user) {
//        PageRequest pageRequest = PageRequest.of(page, size);
        List<Tiket> allByUser = tiketRepository.findAllByUser(user);
        List<TiketItemListResponse> collect = allByUser.stream().map(item -> {
            TiketUserItemListResponse userRes = TiketUserItemListResponse.builder().namaUser(item.getUser().getName()).uuid(item.getUser().getUuid()).build();
            return new TiketItemListResponse(item.getUuid(), item.getStatusTiket(), userRes, item.getJenisTiket().getNama(), item.getJenisTiket().getEvent().getName(), item.getJenisTiket().getEvent().getTanggalAwal(), item.getJenisTiket().getHarga(), item.getJenisTiket().getEvent().getWaktuAwal(), item.getJenisTiket().getEvent().getWaktuAkhir(), item.getJenisTiket().getEvent().getLokasi(), item.getStatusTiket(), item.getPaymentTransaction().getUuid());
        }).toList();
        return new TiketListResponse(collect);
    }

    public TiketPageListResponse getAllTiket(User user, Pageable pageable) {
        Page<Tiket> allByUser = tiketRepository.findAllByUser(user, pageable);
        Page<TiketItemListResponse> map = allByUser.map(item -> {
            TiketUserItemListResponse userRes = TiketUserItemListResponse.builder().namaUser(item.getUser().getName()).uuid(item.getUser().getUuid()).build();
            return new TiketItemListResponse(item.getUuid(), item.getStatusTiket(), userRes, item.getJenisTiket().getNama(), item.getJenisTiket().getEvent().getName(), item.getJenisTiket().getEvent().getTanggalAwal(), item.getJenisTiket().getHarga(), item.getJenisTiket().getEvent().getWaktuAwal(), item.getJenisTiket().getEvent().getWaktuAkhir(), item.getJenisTiket().getEvent().getLokasi(), item.getStatusTiket(), item.getPaymentTransaction().getUuid());
        });
        return new TiketPageListResponse(map);
    }

    public byte[] generateQrTiket(String uuidTiket, User user) throws IOException {
        int imageSize = 200;
        try {
            Tiket tiket = tiketRepository.findByUuid(UUID.fromString(uuidTiket)).orElseThrow();
            if (tiket.getUser().getId().equals(user.getId())) {
                LocalDateTime expiredDateTime = LocalDateTime.of(tiket.getJenisTiket().getEvent().getTanggalAkhir() != null ? tiket.getJenisTiket().getEvent().getTanggalAkhir() : tiket.getJenisTiket().getEvent().getTanggalAwal(), tiket.getJenisTiket().getEvent().getWaktuAkhir());
                String jwtToken = JWT.create()
                        .withIssuer("Ngipen")
                        .withSubject("Ngipen Tiket")
                        .withClaim("tiketVerification", tiket.getTiketVerification().getUuid().toString())
                        .withIssuedAt(new Date())
//                        .withExpiresAt(Date.from(expiredDateTime.atZone(ZoneId.of("Asia/Jakarta")).toInstant()))
                        .withJWTId(UUID.randomUUID().toString())
                        .sign(algorithm);
                Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
                hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                hints.put(EncodeHintType.MARGIN, 0);
                BitMatrix bitMatrix = new MultiFormatWriter().encode(jwtToken, BarcodeFormat.QR_CODE, imageSize, imageSize, hints);
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

    public byte[] generateBarcodeTiket(String uuidTiket, User user) throws WriterException, IOException {
        Tiket tiket = tiketRepository.findByUuid(UUID.fromString(uuidTiket)).orElseThrow();
        int widthSize = 200;
        int heightSize = 100;
        if (tiket.getUser().getId().equals(user.getId())) {
            String textValue = tiket.getTiketVerification().getBarcodetext();
            Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 10);
            BitMatrix bitMatrix = new MultiFormatWriter().encode(textValue, BarcodeFormat.CODE_39, widthSize, heightSize, hints);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
            int scaledWidth = widthSize * 2;

            BufferedImage scaledImage = new BufferedImage(scaledWidth, heightSize + 12, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = scaledImage.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, scaledImage.getWidth(), scaledImage.getHeight());
            g.dispose();
            scaledImage.getGraphics().drawImage(image, 0, 0, scaledWidth, heightSize, null);
            Graphics2D g2d = scaledImage.createGraphics();
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 12)); // Adjust font as needed
            int textX = (scaledWidth - g2d.getFontMetrics().stringWidth(textValue)) / 2;
            int textY = heightSize + 10; // Adjust vertical offset as needed
            g2d.drawString(textValue, textX, textY);
            g2d.dispose();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(scaledImage, "jpeg", bos);
            return bos.toByteArray();
        } else {
            BufferedImage image = new BufferedImage(widthSize, heightSize, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, widthSize, heightSize);

            Font font = new Font("Arial", Font.BOLD,20);
            g.setFont(font);
            g.setColor(Color.BLACK);

            FontMetrics fm = g.getFontMetrics();
            int x = (widthSize - fm.stringWidth("Failed!")) / 2;
            int y = (heightSize - fm.getHeight()) / 2 + fm.getAscent();

            g.drawString("Failed!", x, y);
            g.dispose();
//            log.error(e.getMessage(), e);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpeg", bos);
            return bos.toByteArray();
        }
    }

    public TiketItemListResponse verifyTiket(TiketVerificationRequest payload, User user) throws BadRequestException, JsonProcessingException, JWTVerificationException {
        DecodedJWT decodedJWT = jwtVerifier.verify(payload.getPayload());
        String tiketVerification1 = decodedJWT.getClaim("tiketVerification").asString();
        TiketVerification tiketVerification = tiketVerificationRepository.findByUuid(UUID.fromString(tiketVerification1)).orElseThrow();
        log.info(tiketVerification.toString());
        Event eventByUuid = eventRepository.findEventByUuid(tiketVerification.getTiketToVerification().getJenisTiket().getEvent().getUuid());
            if (eventByUuid != null) {
                Tiket tiket = tiketRepository.findByUuid(tiketVerification.getTiketToVerification().getUuid()).orElseThrow();
                LocalDate tanggalAkhir = tiket.getJenisTiket().getEvent().getTanggalAkhir();
                LocalDate tanggalAwal = tiket.getJenisTiket().getEvent().getTanggalAwal();
                LocalTime waktuAwal = tiket.getJenisTiket().getEvent().getWaktuAwal();
                LocalTime waktuAkhir = tiket.getJenisTiket().getEvent().getWaktuAkhir();
                if (!tiket.getPaymentTransaction().getStatus().equals(PaymentStatus.ACCEPTED)) throw new BadRequestException("Tiket tidak bisa diverifikasi karena pembayaran belum disetujui");
                if (tanggalAkhir != null) {
                    if (LocalDate.now().isAfter(tanggalAkhir))   throw new BadRequestException("Tiket tidak bisa diverifikasi setelah waktu event");
                    if (LocalDate.now().isBefore(tanggalAwal)) throw new BadRequestException("Tiket tidak bisa diverifikasi sebelum waktu event");
                } else {
                    if (LocalDate.now().isAfter(tanggalAwal)) throw new BadRequestException("Tiket tidak bisa diverifikasi setelah waktu event");
                    if (LocalDate.now().isBefore(tanggalAwal)) throw new BadRequestException("Tiket tidak bisa diverifikasi sebelum waktu event");
                }
                if (tiket.getStatusTiket()) throw new BadRequestException("Anda tidak bisa menverifikasi tiket yang sudah terverifikasi");
                tiket.setStatusTiket(true);
                tiketVerification.setVerificationDateTime(LocalDateTime.now());
                tiketVerificationRepository.save(tiketVerification);
                Tiket item = tiketRepository.save(tiket);
                return getTiketItemListResponse(tiket);
//                return new TiketItemListResponse(item.getUuid(), item.getStatusTiket(), userRes, item.getJenisTiket().getNama(), item.getJenisTiket().getEvent().getName(), item.getJenisTiket().getEvent().getTanggalAwal(), item.getJenisTiket().getHarga(), item.getJenisTiket().getEvent().getWaktuAwal(), item.getJenisTiket().getEvent().getWaktuAkhir(), item.getJenisTiket().getEvent().getLokasi(), item.getStatusTiket(), item.getPaymentTransaction().getUuid());
            } else {
                throw new BadRequestException("Event tidak ditemukan");
            }
    }

    public TiketItemListResponse qrScanTiket(TiketVerificationRequest payload, User user) throws BadRequestException, NoSuchElementException {
        DecodedJWT decodedJWT = jwtVerifier.verify(payload.getPayload());
        String tiketVerification1 = decodedJWT.getClaim("tiketVerification").asString();
        TiketVerification tiketVerification = tiketVerificationRepository.findByUuid(UUID.fromString(tiketVerification1)).orElseThrow();
        log.info(tiketVerification.toString());
        Event eventByUuid = eventRepository.findEventByUuid(tiketVerification.getTiketToVerification().getJenisTiket().getEvent().getUuid());
        if (eventByUuid != null) {
            Tiket tiket = tiketRepository.findByUuid(tiketVerification.getTiketToVerification().getUuid()).orElseThrow();
            return getTiketItemListResponse(tiket);
        } else {
            throw new NoSuchElementException("Event tidak ditemukan");
        }
    }

    public TiketItemListResponse getTiketFromUUID(String uuid, User user) {
        try {
            Tiket tiket = tiketRepository.findByUuid(UUID.fromString(uuid)).orElseThrow();
            TiketItemListResponse tiketItemListResponse = getTiketItemListResponse(tiket);
            if (user.getLevel().equals(Level.PEMEGANG_ACARA)) {
                if (tiket.getJenisTiket().getEvent().getPemegangEvent().getId().equals(user.getId())) {
                    return tiketItemListResponse;
                } else {
                    throw new BadCredentialsException("Anda bukan pemegang event dari tiket ini!");
                }
            } else if (user.getLevel().equals(Level.ADMIN)) {
                return tiketItemListResponse;
            } else if (user.getLevel().equals(Level.USER)) {
                if (Objects.equals(tiket.getUser().getId(), user.getId())) {
                    return tiketItemListResponse;
                } else {
                    throw new BadCredentialsException("Anda buka pemegang tiket");
                }
            } else {
                throw new RuntimeException("Failed returning tiket!");
            }
        } catch (Exception exc ){
            throw new DataIntegrityViolationException("Tiket is not found", exc);
        }
    }

    private static @NonNull TiketItemListResponse getTiketItemListResponse(Tiket tiket) {
        TiketUserItemListResponse userRes = TiketUserItemListResponse.builder().namaUser(tiket.getUser().getName()).uuid(tiket.getUser().getUuid()).alamat(tiket.getUser().getAddress()).email(tiket.getUser().getEmail()).nohp(tiket.getUser().getHp()).build();
        return new TiketItemListResponse(tiket.getUuid(), tiket.getStatusTiket(), userRes, tiket.getJenisTiket().getNama(), tiket.getJenisTiket().getEvent().getName(), tiket.getJenisTiket().getEvent().getTanggalAwal(), tiket.getJenisTiket().getHarga(), tiket.getJenisTiket().getEvent().getWaktuAwal(), tiket.getJenisTiket().getEvent().getWaktuAkhir(), tiket.getJenisTiket().getEvent().getLokasi(), tiket.getStatusTiket(), tiket.getPaymentTransaction().getUuid());
    }

    public TiketItemListResponse verifyTiketByUUID(String uuid, int status, User user) throws BadRequestException {
        Tiket tiket = tiketRepository.findByUuid(UUID.fromString(uuid)).orElse(tiketVerificationRepository.findByBarcodeText(uuid).orElseThrow().getTiketToVerification());
        Tiket tiketReturn = null;
        if (user.getLevel() == Level.PEMEGANG_ACARA) {
            Event event = tiket.getJenisTiket().getEvent();
            if (event.getPemegangEvent().getId().equals(user.getId())) {
//                log.info("status : " + status);
                if (status == 1) {
                    if (tiket.getStatusTiket()) throw new BadRequestException("Anda tidak bisa menverifikasi tiket yang sudah terverifikasi");
                }
                tiket.setStatusTiket(status == 1);
                tiketReturn = tiketRepository.save(tiket);
            } else {
                throw new BadRequestException("Anda bukan pemilik event dari tiket ini!");
            }
        } else if (user.getLevel() == Level.ADMIN) {
//            if (tiket.getStatusTiket())
            if (status == 1) {
                if (tiket.getStatusTiket()) throw new BadRequestException("Anda tidak bisa menverifikasi tiket yang sudah terverifikasi");
            }
            tiket.setStatusTiket(status == 1);
            tiketReturn = tiketRepository.save(tiket);
        } else {
            throw new BadRequestException("Anda bukan pemegang event dan admin!");
        }
        return getTiketItemListResponse(tiketReturn);
    }

    public Page<Tiket> getTiketsFromPaymentTransaction(String uuidPt, String uuidEvent, int page, int size) throws NoSuchFieldException {
        PaymentTransaction paymentTransaction = paymentTransactionRepository.findPaymentTransactionByUuid(UUID.fromString(uuidPt)).orElseThrow();
        Event eventByUuid = eventRepository.findEventByUuid(UUID.fromString(uuidEvent));
        if (eventByUuid != null) {
            Pageable pageable = PageRequest.of(page, size);
            return tiketRepository.findTiketByEventAndPaymentTransaction(paymentTransaction.getId(), eventByUuid.getId(), pageable);
        } else {
            throw new NoSuchFieldException("Event tidak ditemukan");
        }
    }

    public User getUserFromPaymentTransaction(String uuidPt, String uuidEvent, User user) throws BadRequestException, NoSuchElementException {
        PaymentTransaction paymentTransaction = paymentTransactionRepository.findPaymentTransactionByUuid(UUID.fromString(uuidPt)).orElseThrow();
        Event eventByUuid = eventRepository.findEventByUuid(UUID.fromString(uuidEvent));
        if (eventByUuid != null) {
            if (user.getLevel() == Level.PEMEGANG_ACARA) {
                if (eventByUuid.getPemegangEvent().getId().equals(user.getId())) {
                    return paymentTransaction.getUser();
                } else {
                    throw new BadRequestException("Anda bukan pemegang event dari event ini");
                }
            } else if (user.getLevel() == Level.ADMIN) {
                return paymentTransaction.getUser();
            } else {
                throw new BadRequestException("Anda bukan pemilik event dan admin");
            }
        } else {
            throw new NoSuchElementException("Event tidak ditemukan");
        }
    }

    public User getUserFromTiket(String uuidTiket, User user) throws BadRequestException {
        Tiket tiket = tiketRepository.findByUuid(UUID.fromString(uuidTiket)).orElseThrow();
        if (user.getLevel().equals(Level.ADMIN)) {
            return tiket.getUser();
        } else if (user.getLevel().equals(Level.PEMEGANG_ACARA)) {
            if (tiket.getJenisTiket().getEvent().getPemegangEvent().getId().equals(user.getId())) {
                return tiket.getUser();
            } else {
                throw new BadRequestException("Anda bukan pemegang event dari event ini");
            }
        } else {
            throw new BadRequestException("Anda bukan pemilik event dan admin");
        }
    }

    public Boolean checkTiketStatus(String uuidTiket, User user) throws BadRequestException, NoSuchFieldException {
        Tiket tiket = tiketRepository.findByUuid(UUID.fromString(uuidTiket)).orElseThrow();
        if (user.getLevel() == Level.USER) {
            if (tiket.getUser().getId().equals(user.getId())) {
                return tiket.getStatusTiket();
            } else {
                throw new BadRequestException("Anda bukan pemilik tiket ini");
            }
        } else if (user.getLevel() == Level.PEMEGANG_ACARA) {
            if (tiket.getJenisTiket().getEvent().getPemegangEvent().getId().equals(user.getId())) {
                return tiket.getStatusTiket();
            } else {
                throw new BadRequestException("Anda bukan pemilik event dari tiket ini");
            }
        } else if (user.getLevel() == Level.ADMIN) {
            return tiket.getStatusTiket();
        }
        return false;
    }

//    public byte[] generateTiketPdf(String uuid) throws DocumentException {
//        StringWriter writer = new StringWriter();
//        String baseUrl = servletContext.getContextPath();
//        final Context context = new Context();
//        context.setVariable("tiketTitle", "Lorem Ipsum");
//        context.setVariable("tiketCategory", "Lorem Ipsum");
//        context.setVariable("tiketDateTime", "Lorem Ipsum");
//        context.setVariable("tiketLocation", "Lorem Ipsum");
//        context.setVariable("tiketTotalCharge", "Lorem Ipsum");
//        context.setVariable("tiketStatus", "Lorem Ipsum");
//        context.setVariable("tiketBarcodeUrl", baseUrl + "/tiket/ef6b9745-f339-4488-8bbc-270a6f3a7228/barcode");
//        log.info(baseUrl + "/tiket/ef6b9745-f339-4488-8bbc-270a6f3a7228/barcode");
//        springTemplateEngine.process("tiket_template", context, writer);
//
//        ITextRenderer renderer = new ITextRenderer();
//        renderer.setDocumentFromString(writer.toString());
//        renderer.layout();
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        renderer.createPDF(outputStream);
//
//        return outputStream.toByteArray();
//    }

//    public SseEmitter getStatusTicket(String uuidTicket, User user) throws NoSuchElementException, BadRequestException {
//        final SseEmitter emitter = new SseEmitter();
//        final Tiket tiket = tiketRepository.findByUuid(UUID.fromString(uuidTicket)).orElseThrow();
//        ExecutorService sseExecutorService = Executors.newSingleThreadExecutor();
//        sseExecutorService.execute(new Runnable() {
//
//            @Override
//            public void run() {
//                if (tiket.getUser().getId().equals(user.getId())) {
//
//                } else {
//                    emitter.completeWithError(new BadRequestException("Anda bukan pemilik tiket ini!"));
//                }
//            }
//        });
//
//    }
}
