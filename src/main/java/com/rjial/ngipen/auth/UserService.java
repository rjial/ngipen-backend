package com.rjial.ngipen.auth;

import com.rjial.ngipen.common.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

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
            createUser.setEmail(request.getEmail());
            createUser.setUuid(UUID.randomUUID());
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

    public Response<UserCreatedUpdatedResponse> updateSelfUser(UserSelfUpdatedRequest request, User user) throws Exception {
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
            createUser.setEmail(request.getEmail());
            if (request.getPassword() != null) {
                createUser.setPassword(passwordEncoder.encode(request.getPassword()));
            }
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

    public UserCreatedUpdatedResponse updateUser(UserUpdatedRequest request, User user, String uuid) throws Exception {
        if (user.getLevel() != Level.ADMIN) {
            throw new BadCredentialsException("Mengubah user harus mempunyai role Admin");
        }
            User updatedUser = userRepository.findByUuid(UUID.fromString(uuid)).orElseThrow();
            updatedUser.setName(request.getName());
            updatedUser.setHp(request.getHp());
            updatedUser.setAddress(request.getAddress());
            updatedUser.setLevel(request.getLevel());
            updatedUser.setEmail(request.getEmail());
            if (!request.getPassword().isEmpty()) {
                updatedUser.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            User save = userRepository.save(updatedUser);
            if (save.getId() > 0) {
                return new UserCreatedUpdatedResponse(save);
            } else {
                throw new DataIntegrityViolationException("User failed updated");
            }
    }

    public Page<User> findAll(Pageable pageable, User user) throws Exception {
        if (!(user.getLevel().equals(Level.ADMIN))) throw new BadCredentialsException("Anda bukan admin");
        try {
            Page<User> findAllUser = userRepository.findAll(pageable);
            return new PageImpl<>(findAllUser.getContent(), pageable, findAllUser.getTotalElements());
        } catch (Exception exception) {
            throw new Exception("Failed to load users : " + exception.getMessage(), exception);
        }
    }
Created
    public User findUserByUUID(String uuid, User user) throws Exception {
        if (!(user.getLevel().equals(Level.ADMIN))) throw new BadCredentialsException("Anda bukan admin");
        try {
            return userRepository.findByUuid(UUID.fromString(uuid)).orElseThrow();
        } catch (Exception exc) {
            throw new Exception("Failed to load user : " + exc.getMessage(), exc);
        }
    }
    public Page<User> searchUser(String query, int page, int size, User user) throws Exception {
        if (!(user.getLevel().equals(Level.ADMIN))) throw new BadCredentialsException("Anda bukan admin");
        try {
            ExampleMatcher exampleMatcher = ExampleMatcher
                    .matchingAny()
                    .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                    .withMatcher("email", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                    .withMatcher("uuid", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                    .withIgnorePaths( "password_user", "id_user");
            Pageable pageable = PageRequest.of(page, size);
            User userSearch = User.builder()
                    .name(query)
                    .email(query)
                    .build();
            Example<User> exampleUser = Example.of(userSearch, exampleMatcher);
            return userRepository.findAll(exampleUser, pageable);
        } catch (Exception exc) {
            throw new Exception("Failed to search user : " + exc.getMessage(), exc);
        }
    }
    public String deleteUser(String uuid, User user) throws BadRequestException, NoSuchElementException {
        if (user.getLevel().equals(Level.ADMIN)) {
            User user1 = userRepository.findByUuid(UUID.fromString(uuid)).orElseThrow();
            userRepository.delete(user1);
            return "Deleting user successfully";
        } else {
            throw new BadRequestException("Anda bukan administrator!");
        }
    }
}
