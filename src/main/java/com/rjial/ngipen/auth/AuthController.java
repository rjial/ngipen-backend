package com.rjial.ngipen.auth;

import com.rjial.ngipen.common.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> login(@RequestBody LoginRequest request) {
        Response<LoginResponse> loginRes = authService.login(request);
        return ResponseEntity.ok(loginRes);
    }

    @PostMapping("/register")
    public ResponseEntity<Response<RegisterResponse>> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Response<RefreshTokenResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
}
