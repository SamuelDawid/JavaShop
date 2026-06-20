package org.javashop.repo;

import lombok.NonNull;
import org.javashop.domain.resources.Electronics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory implemenation of {@link ProductsRepository} backed by a HashMap
 */
public class InMemoryProductRepository implements ProductsRepository{
    private final Map<String,Electronics> electronicsList = new HashMap<>();

    /**
     * Finds a product by its ID.
     * @param id the product ID to search for
     * @return an Optional of the product if found,or empty if not found
     */
    @Override
    public Optional<Electronics> findById(String id) {
        return Optional.ofNullable(electronicsList.get(id));
    }

    /**
     * Returns all products in the repository
     * @return unmodifiable list of all products
     */
    @Override
    public List<Electronics> findAll() {
        return electronicsList.values().stream().toList();
    }

    /**
     * Saves a new product to the repository.
     * Does nto overwrite if a product with the same ID already Exists.
     * @param product the product to save
     * @return true if the product was saved, false if ID already exists
     */
    @Override
    public boolean save( @NonNull Electronics product) {
        return electronicsList.putIfAbsent(product.getId(), product) == null;
    }

    /**
     * Replaces an existing product with a new one.
     *  Does nothing if the given ID does not exist.
     * @param id the ID of the product to replace
     * @param newProduct the new product to store
     */
    @Override
    public void update(String id, @NonNull Electronics newProduct) {
         electronicsList.replace(id,newProduct);
    }

    /**
     * Deletes a product by its ID.
     * @param productId the ID of the product to delete
     * @return true if the product was deleted, false if it did not exist
     */
    @Override
    public boolean delete(String productId) {
        return electronicsList.remove(productId) != null;
    }
}
