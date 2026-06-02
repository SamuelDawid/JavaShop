package org.javashop.repo;

import org.javashop.models.Electronics;

import java.util.List;
import java.util.Optional;

public interface ProductsRepository {
    Optional<Electronics> findById(String id);
    List<Electronics> findAll();
    boolean save(Electronics product);
    void update(String id, Electronics newProduct);
    boolean delete(String productId);
}
