package com.rjial.ngipen.payment;

import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.common.Response;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class CheckoutService {

    @Autowired
    private CheckoutRepository checkoutRepository;

    public Response<CheckoutResponse> getAllCheckout(User user) {
        Response<CheckoutResponse> response = new Response<>();
        List<Checkout> checkouts = checkoutRepository.findCheckoutsByUser(user).orElse(Collections.emptyList());
        CheckoutResponse checkoutResponse = new CheckoutResponse(checkouts);
        response.setData(checkoutResponse);
        response.setMessage("List of checkouts has been returned");
        response.setStatusCode((long) HttpStatus.OK.value());
        return response;
    }

    public Response<Checkout> updateCheckout(int total, UUID uuid, User user) throws BadRequestException {
        Response<Checkout> response = new Response<>();
        Checkout checkout = checkoutRepository.findCheckoutByUuid(uuid).orElseThrow();
        if (checkout.getUser().getId().equals(user.getId())) {
            if (total < 0) {
                throw new BadRequestException("Total shouldn't be less than 0");
            }
            checkout.setTotal(total);
            Checkout savedCheckout = checkoutRepository.save(checkout);
            if (savedCheckout.getId() > 0) {
                response.setData(savedCheckout);
            } else {
                throw new BadRequestException("Checkout failed to updated");
            }
        } else {
            throw new BadCredentialsException("Anda bukan pemilik item checkout ini!");
        }
        response.setMessage("Item checkout berhasil diupdate");
        response.setStatusCode((long) HttpStatus.OK.value());
        return response;
    }

    public Response deleteCheckout(UUID uuid, User user) {
        Response response = new Response<>();
        Checkout checkout = checkoutRepository.findCheckoutByUuid(uuid).orElseThrow();
        if(checkout.getUser() != user) {
            throw new BadCredentialsException("Anda bukan pemilik item checkout ini!");
        }
        checkoutRepository.delete(checkout);
        response.setStatusCode((long) HttpStatus.OK.value());
        response.setMessage("Checkout berhasil dihapus");
        return response;
    }
}
