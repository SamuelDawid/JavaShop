package org.javashop.repo;

import org.javashop.domain.resources.Electronics;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
//Repository

public interface ProductsRepository {
    Optional<Electronics> findById(String id);
    List<Electronics> findAll();
    boolean save(Electronics product);
    void update(String id, Electronics newProduct);
    boolean delete(String productId);
    int decreaseStock(String id, int requestedQty);
}
