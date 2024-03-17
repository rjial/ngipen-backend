package com.rjial.ngipen.payment;

import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.auth.UserRepository;
import com.rjial.ngipen.common.Response;
import com.rjial.ngipen.event.Event;
import com.rjial.ngipen.event.EventRepository;
import com.rjial.ngipen.tiket.JenisTiket;
import com.rjial.ngipen.tiket.JenisTiketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private CheckoutRepository checkoutRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JenisTiketRepository jenisTiketRepository;

    @Autowired
    private EventRepository eventRepository;

    public Response<OrderResponse> orderTiket(List<OrderItemRequest> requests, UUID eventUuid, User user) throws Exception {
        if (!userRepository.existsById(user.getId())) throw new BadCredentialsException("User tidak ditemukan");
        Response<OrderResponse> response = new Response<>();
        try {
            List<Checkout> checkouts = new ArrayList<>();
            requests.forEach(item -> {
                Checkout checkout = new Checkout();
                checkout.setUser(user);
                Event eventByUuid = eventRepository.findEventByUuid(eventUuid);
                checkout.setEvent(eventByUuid);
                JenisTiket jenisTiket = jenisTiketRepository.findById(item.getJenisTiket()).orElseThrow();
                checkout.setJenisTiket(jenisTiket);
                checkout.setTotal(item.getTotal());
                checkouts.add(checkout);
            });
            List<Checkout> checkoutsSaved = checkoutRepository.saveAll(checkouts);
            OrderResponse orderResponse = new OrderResponse(checkoutsSaved);
            response.setData(orderResponse);
            response.setMessage("Pesanan tiket sudah ditambah ke checkout!");
            response.setStatusCode((long) HttpStatus.OK.value());
        } catch (Exception exc) {
            throw new Exception("Pesanan tiket gagal ditambah ke checkout! : " + exc.getMessage(), exc);
        }
        return response;
    }
}
