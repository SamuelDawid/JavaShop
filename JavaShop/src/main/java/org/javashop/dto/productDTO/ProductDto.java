package org.javashop.dto.productDTO;

import java.math.BigDecimal;

public record ProductDto(String id,
                         String name,
                         BigDecimal price,
                         int quantity,
                         boolean available
) {
}
