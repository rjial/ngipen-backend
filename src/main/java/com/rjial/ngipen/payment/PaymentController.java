package com.rjial.ngipen.payment;

import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.common.Response;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Response<List<PaymentHistory>>> getPaymentsFromUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(paymentService.getPaymentFromUser(user));
    }

    @PostMapping("/notification")
    public ResponseEntity<Response<PaymentGatewayNotificationResponse>> notification(@RequestBody PaymentGatewayNotificationRequest request) {
        return ResponseEntity.ok(paymentService.notification(request));
    }
}
