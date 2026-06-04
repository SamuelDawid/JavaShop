package org.javashop.service;

import lombok.RequiredArgsConstructor;
import org.javashop.Exceptions.ProductNotFoundException;
import org.javashop.domain.User.Account;
import org.javashop.domain.resources.Electronics;
import org.javashop.menu.MenuManager;
import org.javashop.models.Cart;
import org.javashop.models.CartItem;
import org.javashop.models.Order;
import org.javashop.models.Voucher;

import java.io.IOException;
import java.util.Scanner;


/**
 * The type Shop cli.
 */
@RequiredArgsConstructor
public class ShopCLI {
    private final ProductManager productManager;
    private final Cart cart;
    private final OrderProcessor orderProcessor;
    private final DiscountService discountService;
    private final Account account;
    private final Scanner scanner = new Scanner(System.in);
    private final MenuManager menuManager = new MenuManager();

    /**
     * Start.
     */
    public void start() throws InterruptedException {
        while (true) {
            menuManager.printMainMenu();
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> showProducts();
                case "2" -> addToCart();
                case "3" -> showCart();
                case "4" -> checkout();
                case "5" -> accountInf();
                case "6" -> pointsExchange();
                case "0" -> {
                    System.out.println("Bye!");
                    orderProcessor.shutDown();
                    return;
                }
                default -> System.out.println("Unknown option");
            }
        }
    }
    private void accountInf(){
        System.out.println(account);
    }
    private void pointsExchange(){
        menuManager.printPointsMenu(account.getPoints(), discountService.getPointsToDiscount());
        System.out.println("Would you like to generate voucher discount? (Always chooses max discount available)(yes/no)");
        String userAnswer = scanner.nextLine();
        if(userAnswer.equalsIgnoreCase("yes")){
            int maxDiscount = discountService.getMaxAvailableDiscount(account.getPoints());
            int pointsToDeduct = maxDiscount * 10;
            Voucher newVoucher = discountService.exchangePoints(account, pointsToDeduct);
            account.setPoints(account.getPoints() - pointsToDeduct);
            account.addVoucherToAccount(newVoucher);
            System.out.println(newVoucher);
        }else System.out.println("Ok, back to main");
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
            Order order = cart.checkout();
             orderProcessor.submitOrderAsync(order)
                    .thenAccept(inv -> {
                        try {
                            FilesHandler.saveToFile(inv,FilesHandler.SAVED_ORDERS_DIRECTORY_PATH);
                            FilesHandler.saveToFile(order,FilesHandler.SAVED_ORDERS_DIRECTORY_PATH);
                            System.out.println("Thank you for your order!");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).exceptionally(e -> {
                        System.out.println("Error: " + e.getMessage());
                        return null;
                    });
    }
}

