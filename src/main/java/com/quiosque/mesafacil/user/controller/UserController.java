package com.quiosque.mesafacil.user.controller;

import com.quiosque.mesafacil.user.dto.CreateUserDTO;
import com.quiosque.mesafacil.user.dto.CreateWaiterDTO;
import com.quiosque.mesafacil.user.dto.ResponseUserDTO;
import com.quiosque.mesafacil.user.dto.WaiterDTO;
import com.quiosque.mesafacil.user.entity.UserEntity;
import com.quiosque.mesafacil.user.service.UserService;
import com.quiosque.mesafacil.user.service.WaiterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final WaiterService waiterService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, WaiterService waiterService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.waiterService = waiterService;
    }


    @PostMapping
    public ResponseEntity<ResponseUserDTO> createUser(@Valid @RequestBody CreateUserDTO dto){
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        return this.userService.createUser(dto);
    }

    @GetMapping
    public List<ResponseUserDTO> getUsers(){
        return this.userService.getUsers();
    }

    @GetMapping("waiters")
    public List<WaiterDTO> getWaiters(@CurrentSecurityContext(expression = "authentication.principal") UserEntity user) {
        return this.userService.getWaiters(user.getId());
    }

    @PostMapping("waiters")
    public ResponseEntity<WaiterDTO> createWaiter(@Valid @RequestBody CreateWaiterDTO dto, @CurrentSecurityContext(expression = "authentication.principal") UserEntity user) {
        return this.waiterService.createWaiter(dto, user.getId());
    }

    @GetMapping("waiters/{id}")
    public WaiterDTO getWaiterById(@PathVariable Long id) {
        return this.waiterService.getWaiterById(id);
    }

    @PutMapping("{id}")
    public ResponseEntity<ResponseUserDTO> updateUser(@PathVariable Long id, @RequestBody CreateUserDTO dto, @CurrentSecurityContext(expression = "authentication.principal") UserEntity user) {
        return this.userService.updateUser(id, dto, user.getId());
    }
}
