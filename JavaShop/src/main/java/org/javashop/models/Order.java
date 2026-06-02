package org.javashop.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Order(UUID orderID,
                    List<Electronics> productsList,
                    LocalDateTime dateTime,
                    BigDecimal total) {
}
