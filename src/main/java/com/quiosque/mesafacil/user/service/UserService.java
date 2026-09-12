package com.quiosque.mesafacil.user.service;

import com.quiosque.mesafacil.configs.JwtService;
import com.quiosque.mesafacil.user.dto.CreateUserDTO;
import com.quiosque.mesafacil.user.dto.ResponseUserDTO;
import com.quiosque.mesafacil.user.dto.WaiterDTO;
import com.quiosque.mesafacil.user.entity.UserEntity;
import com.quiosque.mesafacil.user.mapper.UserMapper;
import com.quiosque.mesafacil.user.repository.UserRepository;
import com.quiosque.mesafacil.user.repository.WaiterRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class UserService {

    private UserMapper mapper;
    private final UserRepository userRepository;
    private final WaiterRepository waiterRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<ResponseUserDTO> createUser(CreateUserDTO dto){
        UserEntity user = UserEntity.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(dto.getRole())
                .build();
        UserEntity savedUser = userRepository.save(user);
        ResponseUserDTO responseUserDTO = mapper.EntityToResponse(savedUser);
        return ResponseEntity.ok(responseUserDTO);
    }

    public List<ResponseUserDTO> getUsers(){
        return userRepository.findAll().stream()
                .map(mapper::EntityToResponse)
                .toList();
    }

    public  List<WaiterDTO> getWaiters(Long id){
        UserEntity adminUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        return waiterRepository.findAllByAdminId(adminUser.getId()).stream()
                .map(mapper::WaiterEntityToWaiter)
                .toList();
    }

    public UserEntity getUserById(Long id){
        return userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(NOT_FOUND, "Usuário não encontrado"));
    }

    public ResponseEntity<ResponseUserDTO> updateUser(Long id, CreateUserDTO dto, Long userId) {
        UserEntity user = getUserById(id);
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName() != null ? dto.getName() : user.getName());
        user.setEmail(user.getEmail());
        user.setPassword(dto.getPassword() != null ? dto.getPassword() : user.getPassword());
        user.setRole(dto.getRole());
        user.setId(userId);

        UserEntity updatedUser = userRepository.save(user);
        ResponseUserDTO responseUserDTO = mapper.EntityToResponse(updatedUser);
        return ResponseEntity.ok(responseUserDTO);
    }
}
