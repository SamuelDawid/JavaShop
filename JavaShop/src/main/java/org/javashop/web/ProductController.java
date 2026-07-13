package org.javashop.web;

import org.javashop.domain.resources.Electronics;
import org.javashop.dto.productDTO.CreateProductRequest;
import org.javashop.dto.productDTO.ProductResponse;
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
    public ResponseEntity<ProductResponse> create(@RequestBody CreateProductRequest request){
        Electronics newItem = new Electronics(request.id(), request.name(), request.price(), request.quantity());
        productManager.addProduct(newItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ProductResponse(newItem.getId(), newItem.getName(), newItem.getPrice(), newItem.getQuantity(), newItem.isAvailable()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@RequestBody CreateProductRequest request, @PathVariable String id){
        if(productManager.findById(id).isEmpty()) return ResponseEntity.notFound().build();
        Electronics update = new Electronics(id,request.name(),request.price(), request.quantity());
        productManager.update(id,update);
        return ResponseEntity.ok(new ProductResponse(update.getId(), update.getName(), update.getPrice(), update.getQuantity(), update.isAvailable()));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id){
        if(productManager.findById(id).isEmpty()) return ResponseEntity.notFound().build();
        productManager.delete(id);
        return ResponseEntity.noContent().build();
    }
}
