package com.quiosque.mesafacil.table.service;

import com.quiosque.mesafacil.table.entity.TableEntity;
import com.quiosque.mesafacil.table.enums.Status;
import com.quiosque.mesafacil.table.repository.TableRepository;
import com.quiosque.mesafacil.table.dtos.CreateTableDTO;
import com.quiosque.mesafacil.table.dtos.ResponseTableDTO;
import com.quiosque.mesafacil.table.mapper.TableMapper;
import com.quiosque.mesafacil.user.entity.UserEntity;
import com.quiosque.mesafacil.user.repository.UserRepository;
import com.quiosque.mesafacil.user.service.WaiterService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@AllArgsConstructor
@Service
public class TableService {

    private final TableRepository mesaRepository;
    private final TableMapper mesaMapper;
    private final WaiterService waiterService;
    private final UserRepository userRepository;

    @Transactional
    public ResponseEntity<ResponseTableDTO> createTable(CreateTableDTO createTableDTO, Long userId) {
        TableEntity mesa = new TableEntity();

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuário não encontrado"));

        UserEntity admin = waiterService.getAdminForUser(user);
        TableEntity mesaExists = mesaRepository
                .findByNumberAndAdminId(createTableDTO.getNumber(), admin.getId())
                .orElse(null);

        if (mesaExists != null && mesaExists.getStatus().equals(Status.ABERTO)) {
            throw new RuntimeException(
                    "Já existe uma mesa " + createTableDTO.getNumber() + " aberta"
            );
        }

        mesa.setCreatedBy(user);

        mesa.setAdmin(admin);

        mesa.setNumber(createTableDTO.getNumber());
        mesa.setStatus(createTableDTO.getStatus());
        mesa.setTitular(createTableDTO.getTitular());

        mesa = mesaRepository.save(mesa);

        return ResponseEntity.ok(mesaMapper.entityToResponse(mesa));
    }

    public List<ResponseTableDTO> getAllTable(Long userId){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuário não encontrado"));
        Long adminId = waiterService.getAdminForUser(user).getId();
        return mesaRepository.findAllByAdminId(adminId).stream()
                .map(mesaMapper::entityToResponse)
                .toList();
    }

    public TableEntity getTableById(Long id, Long userId){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuário não encontrado"));
        Long adminId = waiterService.getAdminForUser(user).getId();
        return mesaRepository.findByIdAndAdminId(id, adminId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Mesa não encontrada"));
    }

    public ResponseEntity<ResponseTableDTO> updateTable(Long id, CreateTableDTO mesadto, Long userId) {
        TableEntity mesa = getTableById(id, userId);

        mesa.setNumber(mesadto.getNumber() != null ? mesadto.getNumber() : mesa.getNumber());
        mesa.setStatus(mesadto.getStatus() != null ? mesadto.getStatus() : mesa.getStatus());
        mesa.setTitular(mesadto.getTitular() != null ? mesadto.getTitular() : mesa.getTitular());

        mesa = mesaRepository.save(mesa);

        return ResponseEntity.ok(mesaMapper.entityToResponse(mesa));
    }
}
