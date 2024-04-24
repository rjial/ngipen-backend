package com.rjial.ngipen.payment;

import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.common.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @PostMapping()
    public ResponseEntity<Response<PaymentOrderResponse>> payment(@AuthenticationPrincipal User user, @RequestBody PaymentOrderRequest request) {
        return ResponseEntity.ok(paymentService.payment(request, user));
    }

    @GetMapping()
    public ResponseEntity<Response<List<PaymentTransaction>>> getPaymentsFromUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(paymentService.getPaymentFromUser(user));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Response<PaymentTransaction>> getPaymentFromUser(@AuthenticationPrincipal User user, @PathVariable("uuid") String uuid) {
        Response<PaymentTransaction> paymentTransactionResponse = new Response<>();
        paymentTransactionResponse.setData(paymentService.getPaymentDetail(uuid, user));
        paymentTransactionResponse.setMessage("Get payment transaction successfully");
        paymentTransactionResponse.setStatusCode((long) HttpStatus.OK.value());
        return ResponseEntity.ok(paymentTransactionResponse);
    }

    @PostMapping("/notification")
    public ResponseEntity<Response<PaymentGatewayNotificationResponse>> notification(@RequestBody PaymentGatewayNotificationRequest request) {
        return ResponseEntity.ok(paymentService.notification(request));
    }
}
