package org.javashop.service;

import lombok.RequiredArgsConstructor;
import org.javashop.Exceptions.ProductNotFoundException;
import org.javashop.domain.resources.Electronics;
import org.javashop.menu.MenuManager;
import org.javashop.models.Cart;
import org.javashop.models.CartItem;
import org.javashop.models.Invoice;
import org.javashop.models.Order;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * The type Shop cli.
 */
@RequiredArgsConstructor
public class ShopCLI {
    private final ProductManager productManager;
    private final Cart cart;
    private final OrderProcessor orderProcessor;
    //
    private final Scanner scanner = new Scanner(System.in);
    private final MenuManager menuManager = new MenuManager();

    /**
     * Start.
     */
    public void start() {
        while (true) {
            menuManager.printMainMenu();
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> showProducts();
                case "2" -> addToCart();
                case "3" -> showCart();
                case "4" -> checkout();
                case "5" -> {
                    System.out.println("Bye!");
                    orderProcessor.shutDown();
                    return;
                }
                default -> System.out.println("Unknown option");
            }
        }
    }

    private void showProducts() {
        for (String s : productManager.returnAllProducts())
            System.out.println(s);
    }

    private void addToCart() {
        while (true) {
            System.out.println("Please provide productID: ");
            String productId = scanner.nextLine();
            System.out.println("How many: ");
            String howMany = scanner.nextLine();
            try {
                Electronics product = productManager.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
                cart.addToCart(product, Integer.parseInt(howMany));
                return;
            } catch (NumberFormatException | ProductNotFoundException e) {
                System.out.println(e.getMessage());
            }

        }

    }

    private void showCart() {
        System.out.println("Your cart: ");
        for (CartItem e : cart.getCart())
            System.out.println(e);

        System.out.println("Total:" + cart.getTotal());
    }

    private void checkout() {
        try {
            Order order = cart.checkout();
            Future<Invoice> future = orderProcessor.submitOrder(order);
            Invoice invoice = future.get();
            FilesHandler.saveToFile(order,FilesHandler.SAVED_ORDERS_DIRECTORY_PATH);
            FilesHandler.saveToFile(invoice,FilesHandler.SAVED_ORDERS_DIRECTORY_PATH);
            System.out.println(invoice);
        } catch (RuntimeException | IOException | ExecutionException | InterruptedException e) {
            System.out.println(e.getMessage());
        }


    }
}

