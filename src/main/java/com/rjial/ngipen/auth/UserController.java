package com.rjial.ngipen.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rjial.ngipen.common.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @GetMapping("")
    public ResponseEntity<Response<Page<User>>> findAllUser(@RequestParam("page") int page, @RequestParam("size") int size, @AuthenticationPrincipal User user) {
        Response<Page<User>> response = new Response<>();
        try {
            Page<User> users = userService.findAll(PageRequest.of(page, size), user);
            response.setData(users);
            response.setMessage("Success Load Users");
            response.setStatusCode((long) HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load users : " + e.getMessage(),e);
        }
    }
    @GetMapping("/{uuid}")
    public ResponseEntity<Response<User>> findUserByUUID(@PathVariable("uuid") String uuid, @AuthenticationPrincipal User user) {
        Response<User> response = new Response<>();
        try {
            User userByUUID = userService.findUserByUUID(uuid, user);
            response.setData(userByUUID);
            response.setMessage("Success Load User " + userByUUID.getName());
            response.setStatusCode((long) HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load user : " + e.getMessage(),e);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Response<Page<User>>> searchUser(@RequestParam("query") String query, @RequestParam int page,@RequestParam int size, @AuthenticationPrincipal User user) {
        Response<Page<User>> response = new Response<>();
        try {
            Page<User> users = userService.searchUser(query, page, size, user);
            response.setData(users);
            response.setMessage("Success searching user ");
            response.setStatusCode((long) HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load user : " + e.getMessage(),e);
        }
    }

    @GetMapping(value = "/detail")
    public ResponseEntity<Response<UserDetailResponse>> userDetail() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(name).orElseThrow();
         log.info(user.getEmail());
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

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Response<String>> deleteUserForAdmin(@AuthenticationPrincipal User user, @PathVariable("uuid") String uuid) throws BadRequestException {
        Response<String> response = new Response<>();
        response.setData(userService.deleteUser(uuid, user));
        response.setMessage(userService.deleteUser(uuid, user));
        response.setStatusCode((long) HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
}
