package com.quiosque.mesafacil.product.mapper;

import com.quiosque.mesafacil.product.dto.ResponseProductDTO;
import com.quiosque.mesafacil.product.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class ProductMapper {

    public ResponseProductDTO productToResponse(ProductEntity product) {
        ResponseProductDTO response = new ResponseProductDTO();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setQuantity(product.getQuantity());
        response.setStatus(product.getStatus());
        response.setTableNumber(product.getMesa().getNumber());
        response.setTitular(product.getMesa().getTitular());

        if (product.getCreatedBy() != null) {
            response.setWaiterName(product.getCreatedBy().getName());
        } else if (product.getAdmin() != null) {
            response.setWaiterName(product.getAdmin().getName());
        }

        return response;
    }
}