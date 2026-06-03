package org.javashop.service;


import lombok.RequiredArgsConstructor;

import org.javashop.domain.resources.Electronics;
import org.javashop.models.Invoice;
import org.javashop.models.InvoiceLine;
import org.javashop.models.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
public class OrderProcessor {
    private final ProductManager productManager;
    private int counter = 1;
    Invoice processOrder(Order order){
        List<InvoiceLine> adjustedInvoice = new LinkedList<>();
        BigDecimal newTotal = BigDecimal.ZERO;
        for (int i = 0; i < order.productsList().size(); i++) {
            Electronics currentProduct = order.productsList().get(i).product();
            int orderedQty = currentProduct.getQuantity();
            int shippedQty = productManager.decreaseStock(currentProduct.getId(), orderedQty);

                InvoiceLine productLine = new InvoiceLine(currentProduct,orderedQty,shippedQty);
                adjustedInvoice.add(productLine);
                newTotal = newTotal.add(currentProduct.getPrice().multiply(BigDecimal.valueOf(shippedQty)));
        }

        String invID = "INV"+order.dateTime().format(DateTimeFormatter.ofPattern("-yyyyMMdd-"))+(counter++);
        return new Invoice(
                invID,
                LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE),
                adjustedInvoice,
                newTotal,
                order.account()
        );
    }
}
