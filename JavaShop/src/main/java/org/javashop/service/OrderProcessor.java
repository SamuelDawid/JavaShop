package org.javashop.service;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.javashop.Exceptions.OrderProcessingException;
import org.javashop.domain.resources.Electronics;
import org.javashop.interfaces.Savable;
import org.javashop.models.Invoice;
import org.javashop.models.InvoiceLine;
import org.javashop.models.Order;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The type Order processor.
 */
@RequiredArgsConstructor
public class OrderProcessor {
    private final ProductManager productManager;
    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final AtomicInteger counter = new AtomicInteger(1);

    /**
     * Process order invoice.
     *
     * @param order the order
     * @return the invoice
     */
    Invoice processOrder(@NonNull Order order){
        List<InvoiceLine> adjustedInvoice = new LinkedList<>();
        BigDecimal newTotal = BigDecimal.ZERO;
        for (int i = 0; i < order.productsList().size(); i++) {
            Electronics currentProduct = order.productsList().get(i).product();
            int orderedQty = order.productsList().get(i).qty();
            int shippedQty = productManager.decreaseStock(currentProduct.getId(), orderedQty);

                InvoiceLine productLine = new InvoiceLine(currentProduct,orderedQty,shippedQty);
                adjustedInvoice.add(productLine);
                newTotal = newTotal.add(currentProduct.getPrice().multiply(BigDecimal.valueOf(shippedQty)));
        }
        if(newTotal.signum() == 0) throw new OrderProcessingException();
        String invID = "INV"+order.dateTime().format(DateTimeFormatter.ofPattern("-yyyyMMdd-"))+(counter.getAndIncrement());
        return new Invoice(
                invID,
                LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE),
                adjustedInvoice,
                newTotal,
                order.account()
        );
    }

    public Future<Invoice> submitOrder(@NonNull Order order){
        return executorService.submit(() -> processOrder(order));
    }
    public void shutDown() throws InterruptedException{
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
    }
    public CompletableFuture<Invoice> submitOrderAsync(Order order){
        return CompletableFuture.supplyAsync(() -> processOrder(order), executorService);
    }
}
