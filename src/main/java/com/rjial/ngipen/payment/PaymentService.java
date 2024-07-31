package com.rjial.ngipen.payment;

import com.midtrans.httpclient.SnapApi;
import com.midtrans.httpclient.error.MidtransError;
import com.rjial.ngipen.auth.Level;
import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.common.NotFoundException;
import com.rjial.ngipen.common.Response;
import com.rjial.ngipen.event.Event;
import com.rjial.ngipen.event.EventRepository;
import com.rjial.ngipen.tiket.Tiket;
import com.rjial.ngipen.tiket.TiketRepository;
import com.rjial.ngipen.tiket.TiketVerification;
import com.rjial.ngipen.tiket.TiketVerificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class PaymentService {

    private Boolean isProduction = false;

    @Autowired
    private CheckoutRepository checkoutRepository;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    @Autowired
    private TiketRepository tiketRepository;

    @Autowired
    private TiketVerificationRepository tiketVerificationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PaymentMidtransComponent paymentMidtransComponent;

    public Response<PaymentOrderResponse> payment(PaymentOrderRequest request, User user) {
        List<Checkout> checkouts = new ArrayList<>();
        Response<PaymentOrderResponse> response = new Response<>();
//        Config snapConfig = Config.builder()
//                .setServerKey(serverKey)
//                .setClientKey(clientKey)
//                .setIsProduction(false)
//                .enableLog(true)
//                .setPaymentOverrideNotification(midtransNotificationUrl)
//                .build();
        try {
            AtomicLong total = new AtomicLong(0);
            request.getOrders().forEach(uuid -> {
                Checkout checkout = checkoutRepository.findCheckoutByUuid(UUID.fromString(uuid)).orElseThrow();
                if (!checkout.getUser().getId().equals(user.getId())) throw new BadCredentialsException("Anda bukan pemilik item checkout ini!");
                if (checkout.getTotal() > 0) {
                    checkouts.add(checkout);
                    total.set(total.get() + (checkout.getTotal() * checkout.getJenisTiket().getHarga()));
                } else {
                    checkoutRepository.delete(checkout);
                }
            });
            UUID paymentUUID = UUID.randomUUID();
            PaymentTransaction paymentTransaction = new PaymentTransaction();
            paymentTransaction.setUuid(paymentUUID);
            paymentTransaction.setTotal(total.intValue());
            paymentTransaction.setStatus(PaymentStatus.WAITING_FOR_VERIFY);
            paymentTransaction.setDate(LocalDateTime.now());
            paymentTransaction.setUser(user);
//            Map<String, Object> midtransParams = new HashMap<>();
//            Map<String, Object> midtransTransactions = new HashMap<>();
//            Map<String, Object> midtransCreditCard = new HashMap<>();
//            Map<String, Object> midtransCustomerDetails = new HashMap<>();
//                midtransCustomerDetails.put("first_name", user.firstName());
//                midtransCustomerDetails.put("last_name", user.lastName());
//                midtransCustomerDetails.put("email", user.getEmail());
//                midtransCustomerDetails.put("phone", user.getHp());
//                midtransParams.put("customer_details", midtransCustomerDetails);
//            midtransTransactions.put("order_id", paymentUUID.toString());
//            midtransTransactions.put("gross_amount", total.toString());
//            midtransCreditCard.put("secure", "true");
//            midtransParams.put("transaction_details", midtransTransactions);
//            midtransParams.put("credit_card", midtransCreditCard);
//            String snapTransactionToken = SnapApi.createTransactionToken(midtransParams, snapConfig);
//            paymentTransaction.setSnapToken(snapTransactionToken);
            PaymentTransaction savedPayment = paymentTransactionRepository.save(paymentTransaction);
            checkouts.forEach(checkout -> {
                PaymentHistory paymentHistory = new PaymentHistory();
                paymentHistory.setPaymentTransaction(paymentTransaction);
                paymentHistory.setUser(checkout.getUser());
                paymentHistory.setEvent(checkout.getEvent());
                paymentHistory.setTotal(checkout.getTotal());
                paymentHistory.setJenisTiket(checkout.getJenisTiket());
//                Tiket tiket = new Tiket();
//                tiket.setJenisTiket(paymentHistory.getJenisTiket());
//                tiket.setUser(paymentHistory.getUser());
//                tiket.setUuid(UUID.randomUUID());
//                tiket.setPaymentTransaction(savedPayment);
//                tiket.setStatusTiket(false);
                saveTiket(paymentHistory, paymentTransaction, checkout.getTotal());
                PaymentHistory savedPaymentHistory = paymentHistoryRepository.save(paymentHistory);
                if(savedPaymentHistory.getId() > 0) {
//                    Tiket savedTiket = tiketRepository.save(tiket);
//                    if (savedTiket.getId() > 0) {
                        checkoutRepository.delete(checkout);
//                    }
                }
            });
            PaymentOrderResponse paymentOrderResponse = new PaymentOrderResponse(savedPayment, null, null);
            response.setData(paymentOrderResponse);
            response.setMessage("Payment successfully");
            response.setStatusCode((long) HttpStatus.OK.value());
        } catch (Exception exc) {
            exc.printStackTrace();
            log.error(exc.getMessage());
            throw new DataIntegrityViolationException("Payment cant be processed! : " + exc.getMessage(), exc);
        }
        return response;
    }

    public Response<List<PaymentTransaction>> getPaymentFromUser(User user) {
        Response<List<PaymentTransaction>> response = new Response<>();
        try {
            response.setData(paymentTransactionRepository.findPaymentTransactionByUser(user));
            response.setMessage("Listing payment transaction successfully");
            response.setStatusCode((long) HttpStatus.OK.value());
        } catch (Exception exc) {
            throw new DataIntegrityViolationException("Listing payment transaction failed", exc);
        }
        return response;
    }
    public PaymentTransaction getPaymentDetail(String uuid, User user) {
        try {
            PaymentTransaction paymentTransaction = paymentTransactionRepository.findPaymentTransactionByUuid(UUID.fromString(uuid)).orElseThrow();
            if (user.getLevel().equals(Level.USER)) {
                if (!paymentTransaction.getUser().getId().equals(user.getId())) {
                    throw new DataIntegrityViolationException("Fetching payment transaction failed");
                }
            }
            return paymentTransaction;
        } catch (Exception exc) {
            throw new DataIntegrityViolationException("Fetching payment transaction failed", exc);
        }
    }

    public JSONObject getPaymentStatus(String uuid, User user) throws BadRequestException, MidtransError, NoSuchElementException {
        PaymentTransaction paymentTransaction = paymentTransactionRepository.findPaymentTransactionByUuid(UUID.fromString(uuid)).orElseThrow();
        if (user.getLevel().equals(Level.USER)) {
            if (!paymentTransaction.getUser().getId().equals(user.getId())) {
                throw new BadRequestException("Fetching payment transaction failed : Anda bukan pemilik dari payment transaction ini!");
            }
            return paymentMidtransComponent.checkTransaction(paymentTransaction.getUuid().toString());
        } else if(user.getLevel().equals(Level.PEMEGANG_ACARA)) {
            AtomicBoolean isAllowToSee = new AtomicBoolean(false);
            paymentTransaction.getTikets().forEach(tiket -> {
                if (tiket.getJenisTiket().getEvent().getPemegangEvent().getId().equals(user.getId())) {
                    isAllowToSee.set(true);
                }
            });
            if (isAllowToSee.get()) {
                return paymentMidtransComponent.checkTransaction(paymentTransaction.getUuid().toString());
            } else {
                throw new BadRequestException("Fetching payment transaction failed : Anda bukan pemilik event dari payment transaction ini!");
            }
        } else {
            return paymentMidtransComponent.checkTransaction(paymentTransaction.getUuid().toString());
        }
    }

    public Response<PaymentGatewayNotificationResponse> notification(PaymentGatewayNotificationRequest request) {
        Response<PaymentGatewayNotificationResponse> response = new Response<>();
        try {
            PaymentGatewayNotificationResponse paymentResponse = new PaymentGatewayNotificationResponse();
            PaymentTransaction paymentTransactionByUuid = paymentTransactionRepository.findPaymentTransactionByUuid(UUID.fromString(request.getOrderId())).orElseThrow();
            if (paymentTransactionByUuid.getId() > 0) {
                paymentResponse.setPaymentType(request.getPaymentType());
                paymentResponse.setCurrency(request.getCurrency());
                paymentResponse.setOrderId(request.getOrderId());
                paymentResponse.setTransactionTime(request.getTransactionTime());
                paymentResponse.setTransactionStatus(request.getTransactionStatus());

                if (request.getTransactionStatus().equals("capture")) {
                    if (request.getFraudStatus().equals("accept")) {
//                        paymentTransactionByUuid.getPaymentHistories().forEach(paymentHistory -> {
//                            saveTiket(paymentHistory, paymentTransactionByUuid, paymentHistory.getTotal());
//                        });
                        paymentTransactionByUuid.setStatus(PaymentStatus.ACCEPTED);
                    }
                } else if (request.getTransactionStatus().equals("settlement")) {
                    if (!paymentTransactionByUuid.getPaymentHistories().isEmpty()) {
//                        for (PaymentHistory paymentHistory : paymentTransactionByUuid.getPaymentHistories()) {
//                            saveTiket(paymentHistory, paymentTransactionByUuid, paymentHistory.getTotal());
//                        }
                    }
                    paymentTransactionByUuid.setStatus(PaymentStatus.ACCEPTED);
                } else if (request.getTransactionStatus().equals("cancel") || request.getTransactionStatus().equals("deny") || request.getTransactionStatus().equals("expire")) {
                    paymentTransactionByUuid.setStatus(PaymentStatus.NOT_ACCEPTED);
                } else if (request.getTransactionStatus().equals("pending")) {
                    paymentTransactionByUuid.setStatus(PaymentStatus.WAITING_FOR_VERIFY);
                }
                paymentTransactionRepository.save(paymentTransactionByUuid);
            } else {
                throw new DataIntegrityViolationException("Data Transaksi tidak ada!");
            }
            response.setData(paymentResponse);
            response.setMessage("Notifikasi dari payment gateway berhasil");
            response.setStatusCode((long) HttpStatus.OK.value());
        }catch (Exception exc) {
            throw new DataIntegrityViolationException("Notifikasi dari payment gateway gagal : " + exc.getMessage(), exc);
        }
        return response;
    }
    private List<Tiket> saveTiket(PaymentHistory paymentHistory, PaymentTransaction paymentTransactionByUuid, int total) {
        List<Tiket> allByPaymentTransaction = tiketRepository.findAllByPaymentTransaction(paymentTransactionByUuid);
        if (allByPaymentTransaction.size() == total) {
            return new ArrayList<>();
        }
        List<Tiket> savedTikets = new ArrayList<>();
        for(int i = 0; i < total; i++) {
            Tiket tiket = new Tiket();
            tiket.setJenisTiket(paymentHistory.getJenisTiket());
            tiket.setPaymentHistory(paymentHistory);
            tiket.setUser(paymentHistory.getUser());
            tiket.setUuid(UUID.randomUUID());
            tiket.setPaymentTransaction(paymentTransactionByUuid);
            tiket.setStatusTiket(false);
            TiketVerification tiketVerification = new TiketVerification();
            tiketVerification.setUuid(UUID.randomUUID());
            tiketVerification.setTiketToVerification(tiket);
            tiket.setTiketVerification(tiketVerificationRepository.save(tiketVerification));
            Tiket savedTiket = tiketRepository.save(tiket);
            if (savedTiket.getId() > 0) {
                savedTikets.add(savedTiket);
            }
        }
        return savedTikets;
    }
    public Page<PaymentTransaction> getPaymentTransactionsByEvent(String uuidEvent, int page, int size, User user) throws BadRequestException {
        Pageable pageable = PageRequest.of(page, size);
        if (user.getLevel().equals(Level.PEMEGANG_ACARA)) {
            Event eventByUuid = eventRepository.findEventByUuid(UUID.fromString(uuidEvent));
            if (eventByUuid != null) {
                if (eventByUuid.getPemegangEvent().getId().equals(user.getId())) {
                    return paymentTransactionRepository.findPaymentTransactionByEvent(eventByUuid.getId(), pageable);
                } else {
                    throw new BadRequestException("Anda bukan pemegang event ini");
                }
            } else {
                throw new NotFoundException("Event tidak ditemukan");
            }
        } else if (user.getLevel().equals(Level.ADMIN)) {
            Event eventByUuid = eventRepository.findEventByUuid(UUID.fromString(uuidEvent));
            if (eventByUuid != null) {
                return paymentTransactionRepository.findPaymentTransactionByEvent(eventByUuid.getId(), pageable);
            } else {
                throw new NotFoundException("Event tidak ditemukan");
            }
        } else {
            throw new BadRequestException("Anda bukan pemegang event ini");
        }
    }

    public PaymentTransaction getPaymentDetail(String uuid, User user, String uuidEvent) throws Exception {
        try {
            PaymentTransaction paymentTransaction = paymentTransactionRepository.findPaymentTransactionByUuid(UUID.fromString(uuid)).orElseThrow();
            Event eventByUuid = eventRepository.findEventByUuid(UUID.fromString(uuidEvent));
            if (eventByUuid != null) {
                if (user.getLevel().equals(Level.ADMIN)) {
                    return paymentTransaction;
                }
                if (user.getLevel().equals(Level.PEMEGANG_ACARA)) {
                    if (eventByUuid.getPemegangEvent().getId().equals(user.getId())) {
                        return paymentTransactionRepository.findFirstByIdAndEvent(user.getId(), eventByUuid.getId(), paymentTransaction.getId()).orElseThrow();
                    } else {
                        throw new BadRequestException("Anda bukan pemegang event ini");
                    }
                }
                throw new BadRequestException("Anda bukan pemegang event ini");
            } else {
                throw new NotFoundException("Event tidak ditemukan");
            }
        } catch (Exception exc) {
            throw new Exception("Fetching payment transaction failed : " + exc.getMessage(), exc);
        }
    }

    public List<PaymentHistory> getPaymentHistoryFromUUID(String uuid, User user) throws BadRequestException, NoSuchElementException {
            PaymentTransaction paymentTransaction = paymentTransactionRepository.findPaymentTransactionByUuid(UUID.fromString(uuid)).orElseThrow();
            if (user.getLevel().equals(Level.USER)) {
                if (paymentTransaction.getUser().getId().equals(user.getId())) {
                    return paymentTransaction.getPaymentHistories();
                } else {
                    throw new BadRequestException("Anda bukan pemilik dari payment transaction ini!");
                }
            } else if(user.getLevel().equals(Level.ADMIN)) {
                return paymentTransaction.getPaymentHistories();
            } else {
                throw new BadRequestException("Anda bukan pemilik dari payment transaction ini!");
            }
    }

    private PaymentTransaction doPaymentSnap(PaymentTransaction paymentTransaction) throws MidtransError, NoSuchElementException {
            User user = paymentTransaction.getUser();
            if (paymentTransaction.getTotal() > 0) {
                Map<String, Object> midtransParams = new HashMap<>();
                Map<String, Object> midtransTransactions = new HashMap<>();
                Map<String, Object> midtransCreditCard = new HashMap<>();
                Map<String, Object> midtransCustomerDetails = new HashMap<>();
                midtransCustomerDetails.put("first_name", user.firstName());
                midtransCustomerDetails.put("last_name", user.lastName());
                midtransCustomerDetails.put("email", user.getEmail());
                midtransCustomerDetails.put("phone", user.getHp());
                midtransParams.put("customer_details", midtransCustomerDetails);
                midtransTransactions.put("order_id", paymentTransaction.getUuid().toString());
                midtransTransactions.put("gross_amount", paymentTransaction.getTotal().toString());
                midtransCreditCard.put("secure", "true");
                midtransParams.put("transaction_details", midtransTransactions);
                midtransParams.put("credit_card", midtransCreditCard);
                log.info(paymentMidtransComponent.clientKey);
                log.info(paymentMidtransComponent.serverKey);
                String snapTransactionToken = SnapApi.createTransactionToken(midtransParams, paymentMidtransComponent.snapConfig());
                paymentTransaction.setSnapToken(snapTransactionToken);
                return paymentTransactionRepository.save(paymentTransaction);
            } else {
                paymentTransaction.setStatus(PaymentStatus.ACCEPTED);
                return paymentTransactionRepository.save(paymentTransaction);
            }
//            if(paymentTransaction.getSnapToken() == null) {
//            } else {
//                return paymentTransaction;
//            }
    }

    public PaymentOrderResponse doPaymentSnapResponse(PaymentTransaction paymentTransaction) throws MidtransError, NoSuchElementException {
        PaymentTransaction transaction = null;
        if (paymentTransaction.getSnapToken() == null) {
            transaction = doPaymentSnap(paymentTransaction);
        } else {
            try {
                JSONObject checkedTransaction = paymentMidtransComponent.checkTransaction(paymentTransaction.getUuid().toString());
                if (Objects.equals(checkedTransaction.getString("status_code"), "404")) {
                    transaction = doPaymentSnap(paymentTransaction);
                } else {
                    transaction = paymentTransaction;
                }
            } catch (MidtransError midtransError) {
                if (midtransError.getStatusCode() == 404) {
                    transaction = doPaymentSnap(paymentTransaction);
                } else {
                    throw midtransError;
                }
            }
        }
        return new PaymentOrderResponse(transaction, transaction.getSnapToken(), paymentMidtransComponent.snapConfig().getClientKey());
    }

    public PaymentOrderResponse doPaymentSnapResponse(String uuidPayment) throws MidtransError, NoSuchElementException {
        PaymentTransaction transaction = paymentTransactionRepository.findPaymentTransactionByUuid(UUID.fromString(uuidPayment)).orElseThrow();
        return doPaymentSnapResponse(transaction);
    }
}
