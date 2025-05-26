package com.michelin.controller.user;


import com.michelin.dto.user.UserRequest;
import com.michelin.dto.user.UserResponse;
import com.michelin.dto.user.UserUpdateRequest;
import com.michelin.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public UserResponse createUser(@RequestBody @Valid UserRequest request){
        return userService.createUser(request);
    }
    @GetMapping
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdateRequest request){
        return userService.updateUser(id, request);
    }
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
    }
}
