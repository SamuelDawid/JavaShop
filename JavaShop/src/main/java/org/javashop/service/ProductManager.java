package org.javashop.service;


import lombok.NonNull;
import org.javashop.models.Electronics;
import org.javashop.repo.InMemoryProductRepository;

public class ProductManager {
    private final InMemoryProductRepository productsRepository;

    public ProductManager(InMemoryProductRepository productsRepository) {
        this.productsRepository = productsRepository;
    }
    public void addProduct(@NonNull Electronics product){
            productsRepository.save(product);
    }
    public void modify(String id,@NonNull Electronics product){
        productsRepository.update(id, product);
    }
    public boolean delete(String id){
        return productsRepository.delete(id);
    }
}
