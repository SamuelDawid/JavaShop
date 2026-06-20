package org.javashop.menu;

import java.util.Map;

/**
 * The type Menu manager.
 */
public class MenuManager {

    /**
     * Print main menu.
     */
    public void printMainMenu() {
        System.out.println("""
                === JAVASHOP ===
                1. Przeglądaj produkty
                2. Dodaj produkt do koszyka
                3. Wyświetl koszyk
                4. Złóż zamówienie
                5. Informacje o Koncie
                6. Wymiana punktów
                0. Wyjście
                """);
    }

    public void printPointsMenu(int currentPoints, Map<Integer, Integer> pointsToDiscount) {
        System.out.println("=== REDEEM POINTS ===");
        System.out.println("Your balance: " + currentPoints + " pts\n");

        pointsToDiscount.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> {
                    int required = e.getKey();
                    int discount = e.getValue();
                    boolean canAfford = currentPoints >= required;
                    System.out.printf("  [%d pts] → %d%% discount %s%n",
                            required,
                            discount,
                            canAfford ? "✓" : "✗ (need " + (required - currentPoints) + " more)");
                });

        System.out.println("\n0. Back");
    }
}
