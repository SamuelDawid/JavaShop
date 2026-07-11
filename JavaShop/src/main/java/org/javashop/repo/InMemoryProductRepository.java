package org.javashop.repo;

import lombok.NonNull;
import org.javashop.Exceptions.ProductNotFoundException;
import org.javashop.domain.resources.Electronics;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryProductRepository implements ProductsRepository {
    private final Map<String, Electronics> electronicsList = new ConcurrentHashMap<>();

    @Override
    public Optional<Electronics> findById(String id) {
        return Optional.ofNullable(electronicsList.get(id));
    }

    /**
     * Returns all products in the repository
     *
     * @return unmodifiable list of all products
     */
    @Override
    public List<Electronics> findAll() {
        return electronicsList.values().stream().toList();
    }

    /**
     * Saves a new product to the repository.
     * Does nto overwrite if a product with the same ID already Exists.
     *
     * @param product the product to save
     * @return true if the product was saved, false if ID already exists
     */
    @Override
    public boolean save(@NonNull Electronics product) {
        return electronicsList.putIfAbsent(product.getId(), product) == null;
    }

    /**
     * Replaces an existing product with a new one.
     * Does nothing if the given ID does not exist.
     *
     * @param id         the ID of the product to replace
     * @param newProduct the new product to store
     */
    @Override
    public void update(String id, @NonNull Electronics newProduct) {
        electronicsList.replace(id, newProduct);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param productId the ID of the product to delete
     * @return true if the product was deleted, false if it did not exist
     */
    @Override
    public boolean delete(String productId) {
        return electronicsList.remove(productId) != null;
    }
    /**
     * Decreases the stock quantity of a product by the requested amount.
     * If requested quantity exceeds available stock. ships only what is available.
     *
     * @param id           the product iD
     * @param requestedQty the quantity requested by the customer
     * @return the actual quantity shipped
     * @throws ProductNotFoundException if no product exists with the given ID
     */
    @Override
    public int decreaseStock(String id, int requestedQty) {
        AtomicInteger shipped = new AtomicInteger();
        electronicsList.compute(id, (key,product) -> {
            if (product == null)
                throw new ProductNotFoundException(id);

            int shippedQty = Math.min(requestedQty, product.getQuantity());
            product.setQuantity(product.getQuantity() - shippedQty);

            shipped.set(shippedQty);
            return product;
        });
        return shipped.get();
    }
}
