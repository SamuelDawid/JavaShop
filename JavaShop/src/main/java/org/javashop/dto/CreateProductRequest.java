package org.javashop.dto;

import java.math.BigDecimal;

public record CreateProductRequest(
        String id,
        String name,
        BigDecimal price,
        int quantity
) {
}
