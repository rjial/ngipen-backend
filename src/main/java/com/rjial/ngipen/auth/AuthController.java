package com.rjial.ngipen.auth;

import com.rjial.ngipen.common.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/detail")
    public ResponseEntity<Response<UserDetailResponse>> getAuthUserDetail(@AuthenticationPrincipal User user) {
        Response<UserDetailResponse> response = new Response<>();
        try {
            UserDetailResponse authUserDetail = authService.getAuthUserDetail(user);
            response.setData(authUserDetail);
            response.setMessage("Success");
            response.setStatusCode((long) HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setStatusCode((long) HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
