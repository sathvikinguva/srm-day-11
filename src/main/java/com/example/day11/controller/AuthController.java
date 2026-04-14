package com.example.day11.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.autoconfigure.JacksonProperties.Json;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.day11.dto.JsonResponse;
import com.example.day11.dto.UserCreateRequest;
import com.example.day11.dto.UserLoginRequest;
import com.example.day11.model.User;
import com.example.day11.service.UserService;
import com.example.day11.util.JwtService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService service;

    @PostMapping("/register")
    public ResponseEntity<JsonResponse<User>> register(@RequestBody UserCreateRequest request) {
        User user = service.createUser(request.getEmail(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(new JsonResponse<>(true, user, null));
    }

    @PostMapping("/login")
    public ResponseEntity<JsonResponse<String>> login(@Valid @RequestBody UserLoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        User user = service.authenticate(email, password);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new JsonResponse<>(false, null, "Invalid credentials"));
        }
        String token = jwtService.generateToken(email);
        return ResponseEntity.ok(new JsonResponse<>(true, token, null));
    }
}
