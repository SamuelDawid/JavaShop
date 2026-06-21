package org.javashop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javashop.Exceptions.EmptyCartException;
import org.javashop.Exceptions.InvalidQuantityException;
import org.javashop.Exceptions.ProductNotFoundException;
import org.javashop.Exceptions.UnavailableProducts;
import org.javashop.discount.DiscountPolicyFactory;
import org.javashop.domain.User.Account;
import org.javashop.domain.resources.Electronics;
import org.javashop.enums.AccountType;
import org.javashop.interfaces.DiscountPolicy;
import org.javashop.interfaces.Savable;
import org.javashop.menu.MenuManager;
import org.javashop.models.*;

import java.io.IOException;
import java.util.Scanner;


/**
 * Command-line interface for the shop application.
 * Handles user interaction for browsing products, managing cart,
 * checkout, account info, and loyalty points exchange.
 */
@Slf4j
@RequiredArgsConstructor
public class ShopCLI {
    private final ProductManager productManager;
    private final Cart cart;
    private final OrderProcessor orderProcessor;
    private final DiscountService discountService;
    private final Account account;
    private final Scanner scanner = new Scanner(System.in);
    private final MenuManager menuManager = new MenuManager();
    private final DiscountPolicyFactory discountPolicyFactory;
    private final PaymentService paymentService;
    private volatile Invoice unpaidInvoice;
    /**
     * Starts the main application loop.
     * Runs until the user selects the exit option.
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
                case "6" -> pointsExchange(account);
                case "7" -> payment();
                case "0" -> {
                    System.out.println("Bye!");
                    orderProcessor.shutDown();
                    return;
                }
                default -> System.out.println("Unknown option");
            }
        }
    }

    private void accountInf() {
        System.out.println(account);
    }

    private void pointsExchange(Account account) {

        if (account.getType() == AccountType.COMPANY) {
            System.out.println("Your account has a 7% flat rate discount!");
            return;
        }

        menuManager.printPointsMenu(account.getPoints(), discountService.getPointsToDiscount());
        System.out.println("Would you like to generate voucher discount? (Always chooses max discount available)(yes/no)");
        String userAnswer = scanner.nextLine();
        if (userAnswer.equalsIgnoreCase("yes")) {
            int maxDiscount = discountService.getMaxAvailableDiscount(account.getPoints());
            if (maxDiscount == 0) {
                System.out.println("Not enough points to redeem!");
                return;
            }
            int pointsToDeduct = discountService.getPointsForDiscount(maxDiscount);
            Voucher newVoucher = discountService.exchangePoints(account, pointsToDeduct);
            account.setPoints(account.getPoints() - pointsToDeduct);
            account.addVoucherToAccount(newVoucher);
            discountService.addVoucherToRepository(newVoucher);
            System.out.println(newVoucher);
        } else System.out.println("Ok, back to main");
    }

    private void showProducts() {
        for (String item : productManager.returnAllProducts())
            System.out.println(item);
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
            } catch (NumberFormatException | ProductNotFoundException | UnavailableProducts |
                     InvalidQuantityException e) {
                log.error("failed: ", e);
            }
        }
    }

    private void showCart() {
        System.out.println("Your cart: ");
        for (CartItem item : cart.getCart()) {
            System.out.println(item);
        }
        System.out.println("Total:" + cart.getCartTotal());
    }

    private void checkout() {
        account.removeExpiredOrUsedVouchers();
        DiscountPolicy policy = discountPolicyFactory.forAccount(account);
        Order order = cart.checkout(policy);
        try {
            orderProcessor.submitOrderAsync(order)
                    .thenAccept(inv -> {
                        saveFiles(inv, order);
                        this.unpaidInvoice = inv;
                    }).exceptionally(e -> {
                        log.error("Order checkout failed", e);
                        return null;
                    });
        } catch (EmptyCartException e) {
            log.error("checkout failed: ", e);
        }

    }
    private void payment(){
        if(unpaidInvoice == null) System.out.println("Nothing to pay");
        System.out.println("""
                 KARTA
                 BLIK
                 PRZELEW
                """);
        String chosenMethod = scanner.nextLine();
        PaymentResult result = paymentService.pay(chosenMethod,unpaidInvoice.total(),unpaidInvoice.userInformation().getAccountNumber(), unpaidInvoice.invoiceNumber());
        if(result.successful()) unpaidInvoice = null;
    }
    private void saveFiles(Savable inv, Savable order) {
        try {
            FilesHandler.saveToFile(inv, FilesHandler.SAVED_ORDERS_DIRECTORY_PATH);
            FilesHandler.saveToFile(order, FilesHandler.SAVED_ORDERS_DIRECTORY_PATH);
        } catch (IOException e) {
            log.error("Saving files failed", e);
        }
    }

}


