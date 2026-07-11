package org.javashop.dto;

import java.math.BigDecimal;

public record ProductResponse(String id, String name, BigDecimal price,int quantity, boolean available) {
}
