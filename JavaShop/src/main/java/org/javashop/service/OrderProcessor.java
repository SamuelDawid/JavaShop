package org.javashop.service;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.javashop.Exceptions.OrderProcessingException;
import org.javashop.domain.resources.Electronics;
import org.javashop.models.Invoice;
import org.javashop.models.InvoiceLine;
import org.javashop.models.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class OrderProcessor {
    private final ProductManager productManager;
    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final AtomicInteger counter = new AtomicInteger(1);

    /**
     * Processes an order synchronously and generates an invoice.
     * Adjust quantities based on actual stock availability and applies
     * any discounts from the original order proportionally to the final total.
     *
     * @param order the order to process
     * @return the generated invoice with adjusted quantitites and final price
     * @throws OrderProcessingException if none of the ordered products could be shipped
     */
    public Invoice processOrder(@NonNull Order order) {
        List<InvoiceLine> adjustedInvoice = new LinkedList<>();
        BigDecimal newTotal = BigDecimal.ZERO;

        for (int i = 0; i < order.productsList().size(); i++) {
            Electronics currentProduct = order.productsList().get(i).product();
            int orderedQty = order.productsList().get(i).qty();
            int shippedQty = productManager.decreaseStock(currentProduct.getId(), orderedQty);

            InvoiceLine productLine = new InvoiceLine(currentProduct, orderedQty, shippedQty);
            adjustedInvoice.add(productLine);
            newTotal = newTotal.add(currentProduct.getPrice().multiply(BigDecimal.valueOf(shippedQty)));
        }
        BigDecimal shippedSubTotal = newTotal;
        BigDecimal discountRation = order.total().divide(order.subTotal(), 4, RoundingMode.HALF_UP);
        BigDecimal finalTotal = shippedSubTotal.multiply(discountRation).setScale(2, RoundingMode.HALF_UP);
        if (newTotal.signum() == 0) throw new OrderProcessingException();
        String invID = "INV" + order.dateTime().format(DateTimeFormatter.ofPattern("-yyyyMMdd-")) + (counter.getAndIncrement());
        return new Invoice(
                invID,
                ZonedDateTime.now(ZoneId.systemDefault()),
                adjustedInvoice,
                finalTotal,
                order.account()
        );
    }

    /**
     * Submits an order for asynchronous processing using a thread pool.
     *
     * @param order the order to process
     * @return a Future containing the generated invoice
     * @deprecated since Task14, use {@link #submitOrderAsync(Order)} instead, scheduled for removal
     */
    @Deprecated(since = "Task14", forRemoval = true)
    public Future<Invoice> submitOrder(@NonNull Order order) {
        return executorService.submit(() -> processOrder(order));
    }

    /**
     * Shuts down the executor service gracefully.
     * Waits up to 5 seconds for current thread is interrupted while waiting
     *
     * @throws InterruptedException
     */
    public void shutDown() throws InterruptedException {
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
    }

    /**
     * Submits an order for asynchronous using CompletableFuture.
     * Preferred over {@link #processOrder(Order)} for non-blocking order handling.
     *
     * @param order the order to process
     * @return a CompletableFuture containing the generated invoice
     */
    public CompletableFuture<Invoice> submitOrderAsync(Order order) {
        return CompletableFuture.supplyAsync(() -> processOrder(order), executorService);
    }
}
