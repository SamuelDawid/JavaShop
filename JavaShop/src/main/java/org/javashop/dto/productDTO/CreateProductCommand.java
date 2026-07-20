package org.javashop.dto.productDTO;

import java.math.BigDecimal;

public record CreateProductCommand(
        String id,
        String name,
        BigDecimal price,
        int quantity
) {
}
