package org.javashop.service;


import lombok.NonNull;
import org.javashop.Exceptions.ProductNotFoundException;
import org.javashop.domain.resources.Electronics;
import org.javashop.repo.InMemoryProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductManager {
    private final InMemoryProductRepository productsRepository;

    public ProductManager(InMemoryProductRepository productsRepository) {
        this.productsRepository = productsRepository;
    }
    public void addAllProducts(@NonNull List<Electronics> list){
        if(!list.isEmpty()){
            for (Electronics e : list)
                productsRepository.save(e);
        }
    }
    public void addProduct(@NonNull Electronics product){
            productsRepository.save(product);
    }
    public void modify(String id,@NonNull Electronics product){
        productsRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        productsRepository.update(id, product);
    }
    public int decreaseStock(String id, int requestedQty){
        Electronics product = productsRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        int shippedQty = Math.min(requestedQty, product.getQuantity());
        int qtyLeft = product.getQuantity() - requestedQty;
        if(qtyLeft < 0)
            product.setQuantity(0);
        else
            product.setQuantity(qtyLeft);
        productsRepository.update(id,product);
        return shippedQty;
    }
    public List<String> returnAllProducts(){
        List<String> listToReturn = new ArrayList<>();
        for (Electronics product : productsRepository.findAll()){
            listToReturn.add(product.toString());
        }
        return listToReturn;
    }
    public Optional<Electronics> findById(String id){
        return productsRepository.findById(id);
    }
    public boolean delete(String id){
        return productsRepository.delete(id);
    }
}
