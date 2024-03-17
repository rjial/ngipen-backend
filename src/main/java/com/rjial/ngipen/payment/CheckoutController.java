package com.rjial.ngipen.payment;

import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.common.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private CheckoutService checkoutService;

    @GetMapping("")
    public ResponseEntity<Response<CheckoutResponse>> getAllCheckout(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(checkoutService.getAllCheckout(user), HttpStatus.OK);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<Response<Checkout>> updateCheckout(@AuthenticationPrincipal User user, @RequestBody CheckoutUpdateRequest request) {
        return new ResponseEntity<>(checkoutService.updateCheckout(request.getTotal(), UUID.fromString(request.getUuid()), user), HttpStatus.OK);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Response> deleteCheckout(@AuthenticationPrincipal User user, @RequestBody CheckoutDeleteRequest request) {
        return new ResponseEntity<>(checkoutService.deleteCheckout(UUID.fromString(request.getUuid()), user), HttpStatus.OK);
    }
}
