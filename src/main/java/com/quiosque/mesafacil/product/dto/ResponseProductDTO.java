package com.quiosque.mesafacil.product.dto;

import com.quiosque.mesafacil.product.enums.ProductEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseProductDTO {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private ProductEnum status;
    private String WaiterName;
    private Integer tableNumber;
    private String titular;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}