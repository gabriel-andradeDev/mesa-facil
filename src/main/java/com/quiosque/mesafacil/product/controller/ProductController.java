package com.quiosque.mesafacil.product.controller;

import com.quiosque.mesafacil.product.dto.CreateProductDTO;
import com.quiosque.mesafacil.product.dto.ResponseProductDTO;
import com.quiosque.mesafacil.product.entity.ProductEntity;
import com.quiosque.mesafacil.product.service.ProductService;
import com.quiosque.mesafacil.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api/product")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ResponseProductDTO> createProduct(@RequestBody CreateProductDTO dto, @CurrentSecurityContext(expression = "authentication.principal") UserEntity user) {
        return productService.createProduct(dto, user.getId());
    }

    @GetMapping
    public List<ResponseProductDTO> getAllProducts(
            @CurrentSecurityContext(expression = "authentication.principal") UserEntity user
    ) {
        return productService.getAllProducts(user.getId());
    }

    @DeleteMapping
    public void deleteProduct(
            @CurrentSecurityContext(expression = "authentication.principal") UserEntity user) {
        productService.deleteAll(user.getId());
    }

    @GetMapping("/table/{mesaId}")
    public List<ResponseProductDTO> getProductsByTableId(
            @PathVariable Long mesaId,
            @CurrentSecurityContext(expression = "authentication.principal") UserEntity user){
        return productService.getProductsByTableId(mesaId, user.getId());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductEntity dto, @CurrentSecurityContext(expression = "authentication.principal") UserEntity user) {
        return productService.updateProduct(id, dto, user.getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, @CurrentSecurityContext(expression = "authentication.principal") UserEntity user) {
        return productService.deleteProduct(id, user.getId());
    }
}
