package org.javashop.web;

import lombok.RequiredArgsConstructor;
import org.javashop.dto.productDTO.CreateProductCommand;
import org.javashop.dto.productDTO.ProductDto;
import org.javashop.service.ProductManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductManager productManager;

    @GetMapping
    public List<ProductDto> all() {
        return productManager.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> byId(@PathVariable String id) {
        return productManager.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductDto> create(@RequestBody CreateProductCommand request) {
        ProductDto dto = productManager.addProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);

    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@RequestBody CreateProductCommand request, @PathVariable String id) {
        ProductDto dto = productManager.update(id, request);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        productManager.delete(id);
        return ResponseEntity.noContent().build();
    }
}
