package com.quiosque.mesafacil.table.controller;

import com.quiosque.mesafacil.table.entity.TableEntity;
import com.quiosque.mesafacil.table.service.TableService;
import com.quiosque.mesafacil.table.dtos.CreateTableDTO;
import com.quiosque.mesafacil.table.dtos.ResponseTableDTO;
import com.quiosque.mesafacil.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api/mesa")
public class TableController {

    private final TableService mesaService;

    @GetMapping
    public List<ResponseTableDTO> getTable(
            @CurrentSecurityContext(expression = "authentication.principal") UserEntity user) {
        return mesaService.getAllTable(user.getId());
    }

    @PostMapping
    public ResponseEntity<ResponseTableDTO> postTable(@RequestBody CreateTableDTO mesas, @CurrentSecurityContext(expression = "authentication.principal") UserEntity user) {
        return mesaService.createTable(mesas, user.getId());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseTableDTO> updateTable(@PathVariable Long id, @RequestBody CreateTableDTO mesas, @CurrentSecurityContext(expression = "authentication.principal") UserEntity user) {
        return mesaService.updateTable(id, mesas, user.getId());
    }
}
