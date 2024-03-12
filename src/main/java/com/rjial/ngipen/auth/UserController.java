package com.rjial.ngipen.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rjial.ngipen.common.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

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

    @PostMapping("")
    public ResponseEntity<Response<UserCreatedUpdatedResponse>> insertUser(@AuthenticationPrincipal User user, @RequestBody UserCreatedRequest request) throws Exception {
        return new ResponseEntity<>(userService.insertUser(request, user), HttpStatus.OK);
    }

    @PutMapping("")
    public ResponseEntity<Response<UserCreatedUpdatedResponse>> updateUser(@AuthenticationPrincipal User user, @RequestBody UserUpdatedRequest request) throws Exception {
        return new ResponseEntity<>(userService.updateUser(request, user), HttpStatus.OK);
    }
}
