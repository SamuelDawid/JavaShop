package org.javashop.menu;

/**
 * The type Menu manager.
 */
public class MenuManager {

    /**
     * Print main menu.
     */
    public void printMainMenu(){
        System.out.println( """
                === JAVASHOP ===
                1. Przeglądaj produkty
                2. Dodaj produkt do koszyka
                3. Wyświetl koszyk
                4. Złóż zamówienie
                5. Wyjście
                """);
    }
}
