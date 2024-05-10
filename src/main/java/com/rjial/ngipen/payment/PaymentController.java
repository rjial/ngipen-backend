package com.rjial.ngipen.payment;

import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.common.NotFoundException;
import com.rjial.ngipen.common.Response;
import com.rjial.ngipen.tiket.Tiket;
import com.rjial.ngipen.tiket.TiketItemListResponse;
import com.rjial.ngipen.tiket.TiketService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    @Autowired
    private TiketService tiketService;

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

    @GetMapping("/event/{uuidEvent}")
    public ResponseEntity<Response<Page<PaymentTransaction>>> getPaymentTransactionByEvent(@AuthenticationPrincipal User user, @RequestParam int page, @RequestParam int size, @PathVariable("uuidEvent") String uuidEvent) {
        Response<Page<PaymentTransaction>> response = new Response<>();
        try {
            response.setData(paymentService.getPaymentTransactionsByEvent(uuidEvent, page, size, user));
            response.setMessage("Get payment transaction successfully");
            response.setStatusCode((long) HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            response.setMessage("Failed to get payment transactions : " + e.getMessage());
            response.setStatusCode((long) HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (BadRequestException e) {
            response.setMessage("Failed to get payment transactions : " + e.getMessage());
            response.setStatusCode((long) HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.setMessage("Failed to get payment transactions : " + e.getMessage());
            response.setStatusCode((long) HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/event/{uuidEvent}/paymenttransaction/{uuidPt}")
    public ResponseEntity<Response<PaymentTransaction>> getPaymentTransactionItemByEvent(@AuthenticationPrincipal User user, @PathVariable("uuidEvent") String uuidEvent, @PathVariable("uuidPt") String uuidPt) {
        Response<PaymentTransaction> response = new Response<>();
        try {
            response.setData(paymentService.getPaymentDetail(uuidPt, user, uuidEvent));
            response.setMessage("Get payment transaction successfully");
            response.setStatusCode((long) HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            response.setMessage("Failed to get payment transactions : " + e.getMessage());
            response.setStatusCode((long) HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.setMessage("Failed to get payment transactions : " + e.getMessage());
            response.setStatusCode((long) HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @GetMapping("/event/{uuidEvent}/paymenttransaction/{uuidPt}/tiket")
    public ResponseEntity<Response<Page<Tiket>>> getTiketsByPaymentTransactionAndEvent(@AuthenticationPrincipal User user, @PathVariable("uuidEvent") String uuidEvent, @PathVariable("uuidPt") String uuidPt, @RequestParam("page") int page, @RequestParam("size") int size) {
        Response<Page<Tiket>> response = new Response<>();
        try {
            response.setData(tiketService.getTiketsFromPaymentTransaction(uuidPt, uuidEvent, page, size));
            response.setMessage("Get tikets successfully");
            response.setStatusCode((long) HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            response.setMessage("Failed to get tikets : " + e.getMessage());
            response.setStatusCode((long) HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.setMessage("Failed to get tikets : " + e.getMessage());
            response.setStatusCode((long) HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
