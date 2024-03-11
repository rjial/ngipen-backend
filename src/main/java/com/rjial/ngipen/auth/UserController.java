package com.rjial.ngipen.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rjial.ngipen.common.Response;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping(value = "/detail")
    public ResponseEntity<Response<UserDetailResponse>> userDetail() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(name).orElseThrow();
        // log.info(user.getEmail());
        UserDetailResponse response = new UserDetailResponse();
        response.setUser(user);
        Response<UserDetailResponse> response1 = new Response<>();
        response1.setData(response);
        response1.setMessage("success");
        response1.setStatusCode(200L);
        return ResponseEntity.ok(response1);
    }
}
