package com.lostpetfinder.controller;

import com.lostpetfinder.dto.UserRequest;
import com.lostpetfinder.dto.UserResponse;
import com.lostpetfinder.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id) { return service.get(id); }

    @GetMapping
    public List<UserResponse> list() { return service.list(); }
}
