package org.javashop.service;


import lombok.NonNull;
import org.javashop.Exceptions.ProductAlreadyExists;
import org.javashop.Exceptions.ProductNotFoundException;
import org.javashop.domain.resources.Electronics;
import org.javashop.repo.ProductsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;


public class ProductManager {
    private final ProductsRepository productsRepository;

    public ProductManager(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    /**
     * Adds all Products from the provided list to the repository.
     * Duplicate products (same iD) are silently ignored.
     *
     * @param list list of products
     * @throws NullPointerException if list is null
     */
    public void addAllProducts(@NonNull List<Electronics> list) {
        if (!list.isEmpty()) {
            for (Electronics e : list)
                productsRepository.save(e);
        }
    }

    /**
     * Adds single Product provided to the repository
     * Duplicate products (same ID) are silently ignored.
     *
     * @param product provided Product
     * @throws NullPointerException if provided product is null
     */
    public void addProduct(@NonNull Electronics product) {
        if (productsRepository.findAll().contains(product)) throw new ProductAlreadyExists(product);
        productsRepository.save(product);
    }

    /**
     * Replaces an existing product in the repository with the provided product.
     *
     * @param id      the ID of the product to update
     * @param product the new product data to replace the existing one
     * @throws ProductNotFoundException if no product exists with the given ID
     * @throws NullPointerException     if product is null
     */
    public void modify(String id, @NonNull Electronics product) {
        productsRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        productsRepository.update(id, product);
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

    /**
     * Returns a list of all Products available in the repository as a String
     *
     * @return nicely formated list of products as a description
     */
    public List<String> returnAllProducts() {
        List<String> listToReturn = new ArrayList<>();
        for (Electronics product : productsRepository.findAll()) {
            listToReturn.add(product.toString());
        }
        return listToReturn;
    }

    /**
     * Returns Optional of a product from repository.
     * If no product found returns Optional.empty()
     *
     * @param id the ID of a product to find
     * @return Optional of a product
     */
    public Optional<Electronics> findById(String id) {
        return productsRepository.findById(id);
    }

    /**
     * Return true if a product with provided ID was successfully deleted from repository
     * If not method will return false
     *
     * @param id the ID of product to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean delete(String id) {
        return productsRepository.delete(id);
    }
}
