package com.messages.userservice.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.messages.userservice.dto.AuthResponse;
import com.messages.userservice.dto.RegisterRequest;
import com.messages.userservice.dto.TransformRequest;
import com.messages.userservice.dto.UserResponse;
import com.messages.userservice.services.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(value = "register_temporal")
    public ResponseEntity<AuthResponse> registerTemporalUser() {

        try {
            return ResponseEntity.ok(userService.registerTemporalUser());

        } catch (Exception e) {
            AuthResponse response = AuthResponse.builder()
                    .build();
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping(value = "getUserByID")
    public ResponseEntity<UserResponse> getUserById(@RequestParam Integer id) {
        try {
            return ResponseEntity.ok(userService.getUser(id));
        } catch (Exception e) {
            UserResponse response = UserResponse.builder()
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping(value = "register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody RegisterRequest requestBody) {
        try {
            return ResponseEntity.ok(userService.registerUser(requestBody));
        } catch (Exception e) {
            AuthResponse response = AuthResponse.builder()
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping(value = "get_user")
    public ResponseEntity<UserResponse> getUser(@RequestParam Integer id) {
        try {
            return ResponseEntity.ok(userService.getUser(id));
        } catch (Exception e) {
            UserResponse response = UserResponse.builder()
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping(value = "get_user_by_name")
    public ResponseEntity<UserResponse> getUserByName(@RequestParam String name) {
        try {
            System.out.println("name: " + name);
            return ResponseEntity.ok(userService.getUserByName(name));
        } catch (Exception e) {
            UserResponse response = UserResponse.builder()
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping(value = "update_user/{id}/{room_id}")
    public ResponseEntity<String> addNewRoom(@PathVariable Integer id, @PathVariable Integer room_id) {
        try {
            userService.addRoom(id, room_id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping(value = "update_user_type/{user_id}")
    public ResponseEntity<AuthResponse> transformUser(@RequestBody TransformRequest requestBody,
            @PathVariable Integer user_id) {
        try {

            return ResponseEntity
                    .ok(userService.transformUser(user_id, requestBody.getUsername(), requestBody.getPassword()));
        } catch (Exception e) {
            AuthResponse response = AuthResponse.builder()
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
    }

}
