package org.javashop.web;

import org.javashop.domain.resources.Electronics;
import org.javashop.dto.CreateProductRequest;
import org.javashop.service.ProductManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductManager productManager;

    public ProductController(ProductManager productManager) {
        this.productManager = productManager;
    }

    @GetMapping
    public List<Electronics> all(){
        return productManager.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Electronics> byId(@PathVariable String id){
        return productManager.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Electronics> create(@RequestBody CreateProductRequest request){
        Electronics newItem = new Electronics(request.id(), request.name(), request.price(), request.quantity());
        productManager.addProduct(newItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(newItem);
    }
}
