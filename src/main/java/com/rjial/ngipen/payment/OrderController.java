package com.rjial.ngipen.payment;

import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.common.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private CheckoutRepository checkoutRepository;

    @Autowired
    private OrderService orderService;

    @PostMapping("/{uuid}")
    public ResponseEntity<Response<OrderResponse>> orderTiket(@PathVariable(name = "uuid") String uuid, @AuthenticationPrincipal User user, @RequestBody OrderRequest request) throws Exception {
        return new ResponseEntity<>(orderService.orderTiket(request.getOrders(), UUID.fromString(uuid), user), HttpStatus.OK);
    }
}
