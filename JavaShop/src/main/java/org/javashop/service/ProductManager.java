package org.javashop.service;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.javashop.Exceptions.ProductAlreadyExists;
import org.javashop.Exceptions.ProductNotFoundException;
import org.javashop.domain.resources.Electronics;
import org.javashop.dto.productDTO.CreateProductCommand;
import org.javashop.dto.productDTO.ProductDto;
import org.javashop.mapper.ProductMapper;
import org.javashop.repo.ProductsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductManager {
    private final ProductsRepository productsRepository;
    private final ProductMapper mapper;

    /**
     * Adds all Products from the provided list to the repository.
     * Duplicate products (same iD) are silently ignored.
     *
     * @param list list of products
     * @throws NullPointerException if list is null
     */
    public void addProducts(@NonNull List<Electronics> list) {
        if (!list.isEmpty()) {
            for (Electronics e : list)
                productsRepository.save(e);
        }
    }

    public ProductDto update(String id, CreateProductCommand productCommand) {
        if (!productsRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        Electronics updated = mapper.toEntity(productCommand);
        productsRepository.save(updated);
        return mapper.toResponse(updated);
    }

    public ProductDto addProduct(@NonNull CreateProductCommand productCommand) {
        Electronics newItem = mapper.toEntity(productCommand);
        if (productsRepository.findAll().contains(newItem)) {
            throw new ProductAlreadyExists(newItem);
        }
        productsRepository.save(newItem);
        return mapper.toResponse(newItem);
    }

    /**
     * Decreases the stock quantity of a product by the requested amount.
     * If requested quantity exceeds available stock. ships only what is available.
     *
     * @param id           the product iD
     * @param requestedQty the quantity requested by the customer
     */
    public int decreaseStock(String id, int requestedQty) {
        return productsRepository.decreaseStock(id, requestedQty);
    }

    public List<ProductDto> findAll() {
        return productsRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    public Optional<ProductDto> findById(String id) {
        return productsRepository.findById(id).map(mapper::toResponse);
    }

    public void delete(String id) {
        Electronics productToDelete = Optional.of(productsRepository.findById(id)).get()
                .orElseThrow(() -> new ProductNotFoundException(id));
        productsRepository.delete(productToDelete);
    }
}
