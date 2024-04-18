package com.rjial.ngipen.auth;

import com.rjial.ngipen.common.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public Response<LoginResponse> login(LoginRequest loginRequest) {
        Response<LoginResponse> response = new Response<>();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
            String token = jwtUtils.generateToken(user);
            String refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setEmail(user.getEmail());
            loginResponse.setToken(token);
            response.setStatusCode(200L);
            response.setData(loginResponse);
            response.setMessage("User successfully created!");
        } catch (Exception e) {
            throw new BadCredentialsException("Login Failed : " + e.getMessage());
            // response.setStatusCode(400L);
            // response.setMessage("Error creating user!");
        }
        return response;
    }

    public Response<RegisterResponse> register(RegisterRequest request) {
        Response<RegisterResponse> response = new Response<>();
        try {
            User user = new User();
            user.setEmail(request.getEmail());
            user.setName(request.getName());
            user.setHp(request.getHp());
            user.setAddress(request.getAddress());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setLevel(Level.USER);
            User userCreate = userRepository.save(user);
            if (userCreate.getId() > 0) {
                RegisterResponse registerResponse = new RegisterResponse();
                registerResponse.setEmail(userCreate.getEmail());
                registerResponse.setHp(userCreate.getHp());
                registerResponse.setAddress(userCreate.getAddress());
                registerResponse.setName(userCreate.getName());
                response.setData(registerResponse);
                response.setMessage("User successfully registered!");
                response.setStatusCode(200L);
            }
        } catch (Exception e) {
            response.setMessage("Failed registering user!");
            response.setStatusCode(500L);
        }
        return response;
    }

    public Response<RefreshTokenResponse> refreshToken(RefreshTokenRequest request) {
        Response<RefreshTokenResponse> response = new Response<>();
        String userEmail = jwtUtils.extractUsername(request.getToken());
        try {
            User user = userRepository.findByEmail(userEmail).orElseThrow();
            if(jwtUtils.isTokenValid(request.getToken(), user)) {
//                String token = jwtUtils.generateToken(user);
                String refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
                RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse();
                refreshTokenResponse.setToken(request.getToken());
                refreshTokenResponse.setRefreshToken(refreshToken);
                response.setData(refreshTokenResponse);
                response.setMessage("Token successfully refreshed");
                response.setStatusCode(200L);
            } else {
                response.setMessage("Error refreshing token");
                response.setStatusCode(400L);
            }
        } catch (Exception e) {
            response.setMessage("Error refreshing token");
            response.setStatusCode(500L);
        }
        return response;
    }

    public UserDetailResponse getAuthUserDetail(User user) {
        try {
            UserDetailResponse userDetailResponse = new UserDetailResponse();
            userDetailResponse.setUser(user);
            return userDetailResponse;
        } catch (Exception exc) {
            throw new BadCredentialsException("Invalid authentication");
        }
    }
}
