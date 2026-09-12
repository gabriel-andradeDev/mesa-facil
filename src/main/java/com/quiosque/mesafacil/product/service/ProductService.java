package com.quiosque.mesafacil.product.service;

import com.quiosque.mesafacil.product.dto.CreateProductDTO;
import com.quiosque.mesafacil.product.dto.ResponseProductDTO;
import com.quiosque.mesafacil.product.entity.ProductEntity;
import com.quiosque.mesafacil.product.repository.ProductRepository;
import com.quiosque.mesafacil.product.mapper.ProductMapper;
import com.quiosque.mesafacil.table.entity.TableEntity;
import com.quiosque.mesafacil.table.service.TableService;
import com.quiosque.mesafacil.user.dto.WaiterDTO;
import com.quiosque.mesafacil.user.entity.UserEntity;
import com.quiosque.mesafacil.user.service.UserService;
import com.quiosque.mesafacil.user.service.WaiterService;
import com.quiosque.mesafacil.user.enums.UserRole;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class ProductService {

    private final WaiterService waiterService;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final ProductMapper mapper;
    private final TableService tableService;

    @Transactional
    public ResponseEntity<ResponseProductDTO> createProduct(
            CreateProductDTO createProductDTO,
            Long userId) {

        UserEntity user = userService.getUserById(userId);
        TableEntity table = tableService.getTableById(createProductDTO.getTableId(), userId);

        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        ProductEntity product = new ProductEntity();

        product.setName(createProductDTO.getName());
        product.setPrice(createProductDTO.getPrice());
        product.setDescription(createProductDTO.getDescription());
        product.setStatus(createProductDTO.getStatus());
        product.setQuantity(createProductDTO.getQuantity());
        product.setMesa(table);

        product.setCreatedBy(user);


        if (user.getRole() == UserRole.ADMIN) {

            product.setAdmin(user);

        } else if (user.getRole() == UserRole.WAITER) {

            WaiterDTO waiterDTO =
                    waiterService.getWaiterUserById(userId);

            if (waiterDTO == null) {
                return ResponseEntity.badRequest().build();
            }

            UserEntity admin =
                    userService.getUserById(waiterDTO.getAdminId());

            if (admin == null || admin.getRole() != UserRole.ADMIN) {
                return ResponseEntity.badRequest().build();
            }

            product.setAdmin(admin);

        } else {
            return ResponseEntity.badRequest().build();
        }

        ProductEntity savedProduct =
                productRepository.save(product);

        ResponseProductDTO response = new ResponseProductDTO();

        response.setId(savedProduct.getId());
        response.setName(savedProduct.getName());
        response.setPrice(savedProduct.getPrice());
        response.setDescription(savedProduct.getDescription());
        response.setStatus(savedProduct.getStatus());
        response.setQuantity(savedProduct.getQuantity());
        response.setWaiterName(user.getName());
        response.setTableNumber(savedProduct.getMesa().getNumber());
        response.setTitular(savedProduct.getMesa().getTitular());


        return ResponseEntity.ok(response);
    }

    public List<ResponseProductDTO> getAllProducts(Long userId){
        UserEntity user = userService.getUserById(userId);


        if (user.getRole() == UserRole.ADMIN) {
            List<ProductEntity> products = productRepository.findAllProduct(user.getId());
            return products.stream().map(mapper::productToResponse).toList();
        } else if (user.getRole() == UserRole.WAITER) {
            WaiterDTO waiterDTO =
                    waiterService.getWaiterUserById(user.getId());
            List<ProductEntity> products = productRepository.findAllProduct(waiterDTO.getAdminId());
            return products.stream().map(mapper::productToResponse).toList();

        }
        return new ArrayList<>();
    }

    @Transactional
    public void deleteAll(Long userId){
        UserEntity user = userService.getUserById(userId);
        Long adminId = waiterService.getAdminForUser(user).getId();
        productRepository.deleteAllByAdminId(adminId);
    }

    public List<ResponseProductDTO> getProductsByTableId(Long tableId, Long userId){
        UserEntity user = userService.getUserById(userId);
        Long adminId = waiterService.getAdminForUser(user).getId();
        tableService.getTableById(tableId, userId);
        List<ProductEntity> products = productRepository.findAllByMesa_IdAndAdminId(tableId, adminId);
        return products.stream().map(mapper::productToResponse).toList();
    }

    public ResponseEntity<ResponseProductDTO> updateProduct(
            Long id,
            ProductEntity dto,
            Long userId
    ) {
        UserEntity user = userService.getUserById(userId);

        ProductEntity product = productRepository.findById(id).orElse(null);

        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        Long adminId = user.getRole() == UserRole.WAITER
                ? waiterService.getAdminForUser(user).getId()
                : user.getId();

        if (!product.getAdmin().getId().equals(adminId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        ProductEntity updatedProduct = new ProductEntity();
        updatedProduct.setName(dto.getName() != null ? dto.getName() : product.getName());
        updatedProduct.setPrice(dto.getPrice() != null ? dto.getPrice() : product.getPrice());
        updatedProduct.setDescription(dto.getDescription() != null ? dto.getDescription() : product.getDescription());
        updatedProduct.setStatus(dto.getStatus() != null ? dto.getStatus() : product.getStatus());
        updatedProduct.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : product.getQuantity());
        updatedProduct.setMesa(dto.getMesa() != null ? dto.getMesa() : product.getMesa());
        updatedProduct.setAdmin(product.getAdmin());
        updatedProduct.setCreatedBy(product.getCreatedBy());
        updatedProduct.setId(product.getId());

        productRepository.save(updatedProduct);

        return ResponseEntity.ok(mapper.productToResponse(updatedProduct));
    }

    public ResponseEntity<Void> deleteProduct(Long id, Long userId) {
        UserEntity user = userService.getUserById(userId);
        ProductEntity product = productRepository.findById(id).orElse(null);

        if (product == null) {
            throw new RuntimeException("Produto não encontrado");
        }

        Long adminId = user.getRole() == UserRole.WAITER
                ? waiterService.getAdminForUser(user).getId()
                : user.getId();

        if (!product.getAdmin().getId().equals(adminId)) {
            throw new RuntimeException("Você não tem permissão para deletar este produto");
        }

        productRepository.delete(product);
        return ResponseEntity.noContent().build();
    }

}
