package com.rjial.ngipen.tiket;

import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.common.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TiketService {

    @Autowired
    private TiketRepository tiketRepository;
//    public Page<Tiket> getAllTiket(int page, int size, User user) {
//        PageRequest pageRequest = PageRequest.of(page, size);
//        return tiketRepository.findAllByUser(user, pageRequest);
//    }
    public TiketListResponse getAllTiket(User user) {
//        PageRequest pageRequest = PageRequest.of(page, size);
        List<Tiket> allByUser = tiketRepository.findAllByUser(user);
        List<TiketItemListResponse> collect = allByUser.stream().map(item -> {
            return new TiketItemListResponse(item.getUuid(), item.getStatusTiket(), item.getUser().getName(), item.getJenisTiket().getNama());
        }).toList();
        return new TiketListResponse(collect);
    }

    public TiketPageListResponse getAllTiket(User user, PageRequest pageRequest) {
        Page<Tiket> allByUser = tiketRepository.findAllByUser(user, pageRequest);
        Page<TiketItemListResponse> map = allByUser.map(item -> {
            return new TiketItemListResponse(item.getUuid(), item.getStatusTiket(), item.getUser().getName(), item.getJenisTiket().getNama());
        });
        return new TiketPageListResponse(map);
    }



    public TiketItemListResponse getTiketFromUUID(String uuid, User user) {
        try {
            Tiket tiket = tiketRepository.findByUuid(UUID.fromString(uuid)).orElseThrow();
            TiketItemListResponse tiketItemListResponse = new TiketItemListResponse(tiket.getUuid(), tiket.getStatusTiket(), tiket.getUser().getName(), tiket.getJenisTiket().getNama());
            if (!Objects.equals(tiket.getUser().getId(), user.getId())) throw new BadCredentialsException("Anda buka pemegang tiket");
            return tiketItemListResponse;
        } catch (Exception exc ){
            throw new DataIntegrityViolationException("Tiket is not found", exc);
        }
    }
}
