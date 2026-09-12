package com.quiosque.mesafacil.product.repository;

import com.quiosque.mesafacil.product.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    @Query("""
    SELECT p FROM ProductEntity p
    WHERE p.admin.id = :id
""")
    List<ProductEntity> findAllProduct(
             Long id
    );

    List<ProductEntity> findAllByMesa_IdAndAdminId(Long mesaId, Long adminId);

    void deleteAllByAdminId(Long adminId);
}
