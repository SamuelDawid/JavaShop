package org.javashop.repo;

import lombok.NonNull;
import org.javashop.domain.resources.Electronics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryProductRepository implements ProductsRepository{
    private final Map<String,Electronics> electronicsList = new HashMap<>();

    @Override
    public Optional<Electronics> findById(String id) {
        return Optional.ofNullable(electronicsList.get(id));
    }

    @Override
    public List<Electronics> findAll() {
        return electronicsList.values().stream().toList();
    }

    @Override
    public boolean save( @NonNull Electronics product) {
        return electronicsList.putIfAbsent(product.getId(), product) == null;
    }
    @Override
    public void update(String id, @NonNull Electronics newProduct) {
         electronicsList.replace(id,newProduct);
    }
    @Override
    public boolean delete(String productId) {
        return electronicsList.remove(productId) != null;
    }
}
