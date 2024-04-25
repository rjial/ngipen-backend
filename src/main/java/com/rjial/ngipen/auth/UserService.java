package com.rjial.ngipen.auth;

import com.rjial.ngipen.common.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Response<UserCreatedUpdatedResponse> insertUser(UserCreatedRequest request, User user) throws Exception {
        if (user.getLevel() != Level.ADMIN) {
            throw new BadCredentialsException("Membuat user harus mempunyai role Admin");
        }
        Response<UserCreatedUpdatedResponse> response = new Response<>();
        try {
            User createUser = new User();
            createUser.setName(request.getName());
            createUser.setHp(request.getHp());
            createUser.setAddress(request.getAddress());
            createUser.setLevel(request.getLevel());
            createUser.setPassword(passwordEncoder.encode(request.getPassword()));
            User save = userRepository.save(createUser);
            if (save.getId() > 0) {
                UserCreatedUpdatedResponse userCreatedUpdatedResponse = new UserCreatedUpdatedResponse(save);
                response.setData(userCreatedUpdatedResponse);
                response.setMessage("User successfully created!");
                response.setStatusCode((long) HttpStatus.OK.value());
            } else {
                throw new DataIntegrityViolationException("User failed created");
            }
        } catch (Exception exc) {
            throw new Exception("User failed created : ", exc);
        }
        return response;
    }

    public Response<UserCreatedUpdatedResponse> updateUser(UserUpdatedRequest request, User user) throws Exception {
        if (user.getLevel() != Level.ADMIN) {
            throw new BadCredentialsException("Mengubah user harus mempunyai role Admin");
        }
        Response<UserCreatedUpdatedResponse> response = new Response<>();
        try {
            User createUser = userRepository.findById(request.getId()).orElseThrow();
            createUser.setName(request.getName());
            createUser.setHp(request.getHp());
            createUser.setAddress(request.getAddress());
            createUser.setLevel(request.getLevel());
            createUser.setPassword(passwordEncoder.encode(request.getPassword()));
            User save = userRepository.save(createUser);
            if (save.getId() > 0) {
                UserCreatedUpdatedResponse userCreatedUpdatedResponse = new UserCreatedUpdatedResponse(save);
                response.setData(userCreatedUpdatedResponse);
                response.setMessage("User successfully updated!");
                response.setStatusCode((long) HttpStatus.OK.value());
            } else {
                throw new DataIntegrityViolationException("User failed updated");
            }
        } catch (Exception exc) {
            throw new Exception("User failed updated : ", exc);
        }
        return response;
    }

    public Page<User> findAll(Pageable pageable, User user) throws Exception {
//        if (!(user.getLevel().equals(Level.ADMIN))) throw new BadCredentialsException("Anda bukan admin");
        try {
            Page<User> findAllUser = userRepository.findAll(pageable);
            return new PageImpl<>(findAllUser.getContent(), pageable, findAllUser.getTotalElements());
        } catch (Exception exception) {
            throw new Exception("Failed to load users : " + exception.getMessage(), exception);
        }
    }
}
