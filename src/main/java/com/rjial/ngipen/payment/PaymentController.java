package com.rjial.ngipen.payment;

import com.midtrans.httpclient.error.MidtransError;
import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.common.NotFoundException;
import com.rjial.ngipen.common.Response;
import com.rjial.ngipen.tiket.Tiket;
import com.rjial.ngipen.tiket.TiketItemListResponse;
import com.rjial.ngipen.tiket.TiketService;
import org.apache.coyote.BadRequestException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

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

    @GetMapping("/{uuid}/status")
    public ResponseEntity<Response<String>> getPaymentStatus(@AuthenticationPrincipal User user, @PathVariable("uuid") String uuid) {
        Response<String> jsonObjectResponse = new Response<>();
        try {
            jsonObjectResponse.setData(paymentService.getPaymentStatus(uuid, user).toString());
            jsonObjectResponse.setMessage("Successfully returning payment status from payment gateway");
            jsonObjectResponse.setStatusCode((long) HttpStatus.OK.value());
            return ResponseEntity.ok(jsonObjectResponse);
        } catch (MidtransError e) {
            jsonObjectResponse.setMessage("Failed returning payment status from payment gateway : " + e.getMessage());
            jsonObjectResponse.setStatusCode((long) HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonObjectResponse);
        } catch (BadRequestException e) {
            jsonObjectResponse.setMessage(e.getMessage());
            jsonObjectResponse.setStatusCode((long) HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonObjectResponse);
        } catch (NoSuchElementException e) {
            jsonObjectResponse.setMessage("Failed returning payment status from payment gateway : " + e.getMessage());
            jsonObjectResponse.setStatusCode((long) HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(jsonObjectResponse);
        } catch (Exception e) {
            jsonObjectResponse.setMessage("Failed returning payment status from server : " + e.getMessage());
            jsonObjectResponse.setStatusCode((long) HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonObjectResponse);
        }
    }

    @GetMapping("/{uuid}/histories")
    public ResponseEntity<Response<List<PaymentHistory>>> getPaymentHistories(@PathVariable("uuid") String uuid, @AuthenticationPrincipal User user) {
        Response<List<PaymentHistory>> response = new Response<>();
        try {
            response.setData(paymentService.getPaymentHistoryFromUUID(uuid, user));
            response.setMessage("Successfully returning payment history");
            response.setStatusCode((long) HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (BadRequestException e) {
            response.setMessage("Failed returning payment history : " + e.getMessage());
            response.setStatusCode((long) HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (NoSuchElementException e) {
            response.setMessage("Failed returning payment history : " + e.getMessage());
            response.setStatusCode((long) HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.setMessage("Failed returning payment history from server : " + e.getMessage());
            response.setStatusCode((long) HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
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

    @GetMapping("/event/{uuidEvent}/paymenttransaction/{uuidPt}/user")
    public ResponseEntity<Response<User>> getUserByPaymentTransactionAndEvent(@AuthenticationPrincipal User user, @PathVariable("uuidEvent") String uuidEvent, @PathVariable("uuidPt") String uuidPt) {
        Response<User> response = new Response<>();
        try {
            response.setData(tiketService.getUserFromPaymentTransaction(uuidPt, uuidEvent,user));
            response.setMessage("Get user successfully");
            response.setStatusCode((long) HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (NotFoundException | NoSuchElementException e) {
            response.setMessage("Failed to get user : " + e.getMessage());
            response.setStatusCode((long) HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.setMessage("Failed to get user : " + e.getMessage());
            response.setStatusCode((long) HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/pay/{uuid}")
    public ResponseEntity<Response<PaymentOrderResponse>> doPaySnap(@AuthenticationPrincipal User user, @PathVariable("uuid") String uuidPaymentTransaction) {
        Response<PaymentOrderResponse> response = new Response<>();
        try {
            response.setData(paymentService.doPaymentSnapResponse(uuidPaymentTransaction));
            response.setMessage("Successful to reach payment gateway");
            response.setStatusCode((long) HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (MidtransError e) {
            response.setMessage("Failed to reach payment gateway : " + e.getMessage());
            response.setStatusCode((long) HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (NoSuchElementException e) {
            response.setMessage("Failed to reach payment gateway : " + e.getMessage());
            response.setStatusCode((long) HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.setMessage("Failed to reach payment gateway : " + e.getMessage());
            response.setStatusCode((long) HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
