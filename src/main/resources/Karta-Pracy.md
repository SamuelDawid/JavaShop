# Karta pracy — Projekt: Konsolowy Menadżer Sklepu „JavaShop"

> **Zasady gry:**
> - Nie kopiuj kodu z internetu. Pisz sam, zaglądaj tylko gdy utkniesz na ponad 15 minut.
> - Każda część buduje na poprzedniej — nie pomijaj kolejności.
> - Wszystkie klasy twórz w pakiecie `com.example.javashop`.
> - Brak gotowego kodu — to Twoja robota. 💪

---

## Czym jest JavaShop?

Budujesz konsolowy system zarządzania małym sklepem internetowym. Użytkownik może:
- dodawać produkty do magazynu,
- dodawać produkty do koszyka,
- składać zamówienia,
- przeglądać historię zamówień,
- zapisywać i wczytywać stan magazynu z pliku.

Na końcu dorzucisz wątek działający w tle, który co jakiś czas sprawdza stan magazynu.

---

## Część 1 — Klasa `Product` (OOP: klasy, pola, konstruktory, gettery)

**Cel:** Stworzyć pierwszą klasę domeny. Nauczyć się enkapsulacji: pola prywatne, dostęp przez metody.

**Teoria w pigułce:**
Klasa to szablon. Obiekt to konkretny egzemplarz. Pola opisują *stan* (co obiekt wie), metody opisują *zachowanie* (co obiekt potrafi).
Enkapsulacja = chowanie pól za `private` i udostępnianie ich przez gettery/settery. Konstruktor to specjalna metoda wywoływana przy `new`.

**Klasy i API, których będziesz używać:**
- `String`, `double`, `int` — typy pól
- `Math.round()` — do zaokrąglania ceny do 2 miejsc po przecinku
- `String.format()` — do ładnego wypisania produktu w `toString()`
- `Objects.requireNonNull()` — walidacja w konstruktorze

### Zadanie

Utwórz plik: `src/main/java/com/example/javashop/product/Product.java`

Klasa `Product` powinna mieć:

**Pola (wszystkie `private`):**
- `id` — unikalny identyfikator (`int`)
- `name` — nazwa produktu (`String`)
- `price` — cena (`double`)
- `quantity` — ilość w magazynie (`int`)

**Konstruktor:**
- przyjmuje `id`, `name`, `price`, `quantity`
- sprawdza, czy `name` nie jest `null` ani pusty — jeśli jest, rzuć `IllegalArgumentException`
- sprawdź, czy `price >= 0` i `quantity >= 0`

**Metody:**
- gettery dla wszystkich pól
- setter tylko dla `price` i `quantity` (cena i ilość mogą się zmieniać, `id` i `name` — nie)
- `toString()` — zwraca ładnie sformatowany string, np.: `[1] Laptop - 2999.99 zł (5 szt.)`
- `isAvailable()` — zwraca `true` jeśli `quantity > 0`

### Krok po kroku

1. Napisz klasę z polami i konstruktorem. Uruchom — nie ma jeszcze `main`, ale powinna się kompilować.
2. Dodaj gettery. Napisz tymczasowy `main` w osobnej klasie `Main.java` i stwórz 3 produkty. Wypisz je przez `System.out.println()`.
3. Dodaj walidację w konstruktorze. Spróbuj stworzyć produkt z pustą nazwą — co się dzieje?
4. Dodaj `toString()`. Użyj `String.format("%-20s %8.2f zł", ...)` żeby wyrównać kolumny.
5. Napisz `isAvailable()`. Stwórz produkt z `quantity = 0` i sprawdź.

### Pytania do przemyślenia

1. Dlaczego `id` nie ma settera? Co by się stało, gdyby kod zewnętrzny mógł zmienić `id` po stworzeniu obiektu?
2. Czy konstruktor powinien zaokrąglać cenę do 2 miejsc, czy to zadanie dla warstwy wypisującej?
3. Co się stanie, jeśli ustawisz `quantity = -5` przez setter? Jak temu zapobiec?

---

## Część 2 — Dziedziczenie: typy produktów (`Electronics`, `Food`)

**Cel:** Rozszerzyć `Product` o specjalizowane podklasy. Zrozumieć `extends`, `super`, `@Override`.

**Teoria w pigułce:**
Dziedziczenie = „jest rodzajem" (`Electronics` jest `Product`). Podklasa dziedziczy wszystkie pola i metody klasy nadrzędnej, może dodać własne i nadpisać istniejące przez `@Override`. `super(...)` wywołuje konstruktor rodzica — musi być pierwszą linią konstruktora podklasy.

**Klasy i API, których będziesz używać:**
- `super(...)` — wywołanie konstruktora rodzica
- `@Override` — nadpisanie metody
- `LocalDate` (`java.time`) — data ważności w `Food`
- `LocalDate.now()`, `ChronoUnit.DAYS.between()` — obliczanie dni do ważności

### Zadanie

Utwórz dwa pliki w pakiecie `com.example.javashop.product`:

**`Electronics.java`** — rozszerza `Product`, dodaje:
- pole `warrantyMonths` (`int`) — liczba miesięcy gwarancji
- konstruktor przyjmujący wszystkie pola rodzica + `warrantyMonths`
- getter dla `warrantyMonths`
- `@Override toString()` — dołącz info o gwarancji: `[1] Laptop - 2999.99 zł (5 szt.) | Gwarancja: 24 mies.`

**`Food.java`** — rozszerza `Product`, dodaje:
- pole `expiryDate` (`LocalDate`) — data ważności
- konstruktor przyjmujący wszystkie pola rodzica + `expiryDate`
- metodę `daysUntilExpiry()` — ile dni do wygaśnięcia od dziś
- metodę `isExpired()` — czy produkt jest przeterminowany
- `@Override toString()` — dołącz info o dacie ważności

### Krok po kroku

1. Napisz `Electronics`. W `Main` stwórz kilka elektroniki i wypisz. Sprawdź czy `toString` działa.
2. Napisz `Food`. Stwórz produkt z `expiryDate = LocalDate.now().minusDays(1)` — `isExpired()` powinno zwrócić `true`.
3. W `Main` zrób tablicę `Product[] products = {laptop, milk, phone, bread}` — mieszaj typy.
4. W pętli `for` wypisz każdy produkt. Zauważ: wywołuje się odpowiedni `toString()` — to **polimorfizm**.
5. Dodaj `instanceof` check: dla każdego `Food` w tablicy wypisz ile dni do ważności.

### Pytania do przemyślenia

1. Dlaczego `Food` i `Electronics` mają `super(id, name, price, quantity)` w konstruktorze? Co by się stało bez tego?
2. Co to znaczy, że `Product[] products` może trzymać obiekty `Electronics` i `Food`?
3. Kiedy warto używać `instanceof`, a kiedy to sygnał złego projektu?

---

## Część 3 — Interfejsy i klasy abstrakcyjne (`Discountable`, `Printable`)

**Cel:** Zdefiniować kontrakt przez interfejs. Zrozumieć różnicę między interfejsem a klasą abstrakcyjną.

**Teoria w pigułce:**
Interfejs = kontrakt — mówi CO klasa musi umieć, nie JAK. Klasa może implementować wiele interfejsów (`implements A, B`), ale dziedziczyć tylko po jednej klasie. Klasa abstrakcyjna = częściowa implementacja, nie można jej zinstancjonować. Metody `default` w interfejsie mają gotową implementację — podklasa może, ale nie musi ich nadpisać.

**Klasy i API, których będziesz używać:**
- `interface` — definicja kontraktu
- `default` metody w interfejsie
- `abstract class` — jeśli zdecydujesz się z niej skorzystać
- `Math.max(0, ...)` — przy obliczaniu ceny po zniżce (nie może być ujemna)

### Zadanie

**`Discountable.java`** — interfejs:
- metoda `applyDiscount(double percentage)` — przyjmuje procent zniżki (0-100), zwraca cenę po zniżce
- metoda `default getDiscountLabel()` — zwraca napis `"Promocja!"` (domyślna implementacja)

**`Printable.java`** — interfejs:
- metoda `printDetails()` — wypisuje pełne szczegóły na konsolę

Niech `Electronics` i `Food` implementują oba interfejsy.
W `Electronics.printDetails()` wypisz też gwarancję.
W `Food.printDetails()` wypisz też datę ważności i czy produkt jest przeterminowany.

### Krok po kroku

1. Napisz interfejsy. Skompiluj — interfejs sam w sobie jest kompletny.
2. Dodaj `implements Discountable, Printable` do `Electronics`. IntelliJ podkreśli klasę na czerwono — wciśnij `Alt+Enter` → `Implement methods`. Zauważ co się wygenerowało.
3. Zaimplementuj `applyDiscount`: odejmij procent od ceny. Użyj `Math.max(0, ...)` żeby nie wyjść poniżej 0.
4. Zaimplementuj `printDetails()` z pełnymi informacjami o produkcie.
5. W `Main` wywołaj `printDetails()` na każdym produkcie i przetestuj `applyDiscount(20)`.

### Pytania do przemyślenia

1. Dlaczego `applyDiscount` jest w interfejsie, a nie od razu w klasie `Product`?
2. Kiedy użyłbyś klasy abstrakcyjnej zamiast interfejsu? Podaj przykład z tego projektu.
3. Co się stanie, jeśli dwa interfejsy mają `default` metodę o tej samej nazwie, a klasa implementuje oba?

---

## Część 4 — Kolekcje: `Inventory` i `Cart` (`ArrayList`, `HashMap`)

**Cel:** Przechowywać wiele produktów i operować na nich. Zrozumieć kiedy `ArrayList` a kiedy `HashMap`.

**Teoria w pigułce:**
`ArrayList` = lista z indeksami, szybkie dodawanie i iterowanie, wolne szukanie po wartości. `HashMap` = mapa klucz→wartość, szybkie szukanie po kluczu (`O(1)`). Generalnie: jeśli szukasz „po ID" — HashMap. Jeśli iterujesz po wszystkim — ArrayList. `Collections` to klasa narzędziowa z metodami `sort`, `unmodifiableList`, `frequency` itp.

**Klasy i API, których będziesz używać:**
- `ArrayList<Product>` — lista produktów w koszyku
- `HashMap<Integer, Product>` — magazyn (klucz = id produktu)
- `Collections.unmodifiableList()` — zwrócenie listy tylko do odczytu
- `Iterator` lub pętla `for-each` — iterowanie
- `Optional<Product>` — zwracanie wyniku wyszukiwania bez `null`

### Zadanie

**`Inventory.java`** — klasa magazynu:
- pole `products` jako `HashMap<Integer, Product>`
- `addProduct(Product p)` — dodaje produkt do mapy (klucz = `p.getId()`)
- `removeProduct(int id)` — usuwa produkt z mapy
- `findById(int id)` — zwraca `Optional<Product>`
- `findByName(String name)` — przeszukuje wartości mapy, zwraca `Optional<Product>`
- `getAllProducts()` — zwraca `Collections.unmodifiableList(...)` — kopię listy wartości
- `getLowStockProducts(int threshold)` — zwraca `ArrayList` produktów z `quantity <= threshold`

**`Cart.java`** — klasa koszyka:
- pole `items` jako `ArrayList<Product>` (produkty dodane przez klienta)
- `addItem(Product p)` — dodaje do listy
- `removeItem(int id)` — usuwa po id
- `getTotalPrice()` — sumuje ceny wszystkich produktów
- `clear()` — czyści koszyk
- `getItems()` — zwraca niemodyfikowalną listę

### Krok po kroku

1. Napisz `Inventory`. Dodaj kilka produktów, wypisz wszystkie przez `getAllProducts()`.
2. Przetestuj `findByName("Laptop")` — co zwraca `Optional`? Użyj `.orElse(null)` lub `.ifPresent(...)`.
3. Napisz `Cart`. Dodaj 3 produkty, wywołaj `getTotalPrice()`.
4. Dodaj produkt dwa razy do koszyka — czy `getTotalPrice()` liczy go podwójnie? Czy to zamierzone?
5. Przetestuj `getLowStockProducts(3)` — dodaj produkty z różnym `quantity`.

### Pytania do przemyślenia

1. Dlaczego `Inventory` używa `HashMap`, a `Cart` używa `ArrayList`? Odwróć — co by się zepsuło?
2. Co to `Optional` i dlaczego jest lepszy od zwracania `null`?
3. Dlaczego `getAllProducts()` zwraca `unmodifiableList`? Co by się stało bez tego?

---

## Część 5 — Generyki: `Repository<T>`

**Cel:** Napisać jeden ogólny pojemnik zamiast osobnych klas dla każdego typu danych.

**Teoria w pigułce:**
Generyki (`<T>`) pozwalają pisać kod który działa dla dowolnego typu, a błędy typowania wyłapywane są w czasie kompilacji, nie działania. `T` to placeholder — przy użyciu klasy podajesz konkretny typ, np. `Repository<Product>`. Ograniczenia: `<T extends Comparable<T>>` — tylko typy które można porównać.

**Klasy i API, których będziesz używać:**
- `<T>` — parametr typowy
- `List<T>`, `ArrayList<T>` — wewnętrzna kolekcja
- `Comparator<T>` — sortowanie generyczne
- `T extends ...` — bounded type parameter (ograniczony typ)

### Zadanie

Utwórz plik: `src/main/java/com/example/javashop/util/Repository.java`

Klasa `Repository<T>` — generyczny pojemnik na dowolne obiekty:
- pole `items` jako `ArrayList<T>` (prywatne)
- `add(T item)` — dodaje element
- `remove(T item)` — usuwa element
- `getAll()` — zwraca niemodyfikowalną listę
- `size()` — liczba elementów
- `findFirst(java.util.function.Predicate<T> predicate)` — zwraca `Optional<T>` — pierwszy element spełniający warunek
- `filter(java.util.function.Predicate<T> predicate)` — zwraca `List<T>` z pasującymi elementami
- metoda statyczna `<T extends Comparable<T>> T findMax(List<T> list)` — zwraca największy element

### Krok po kroku

1. Napisz klasę z polami i `add`/`remove`/`getAll`. Stwórz `Repository<String>` w `Main` i dodaj kilka stringów.
2. Dodaj `findFirst` z `Predicate<T>`. W `Main`: `repo.findFirst(s -> s.startsWith("A"))`.
3. Stwórz `Repository<Product>` i użyj `findFirst(p -> p.getPrice() > 100)`.
4. Napisz `findMax` jako metodę statyczną z `<T extends Comparable<T>>`. Przetestuj na `List<Integer>`.
5. Zastanów się: czy `Inventory` mogłoby dziedziczyć po `Repository<Product>`? Jakie byłyby plusy i minusy?

### Pytania do przemyślenia

1. Co oznacza `<T extends Comparable<T>>`? Dlaczego `findMax` tego wymaga?
2. Czy `Repository<int>` zadziała? Dlaczego tak/nie?
3. Czym różni się `List<Product>` od `List<? extends Product>`?

---

## Część 6 — Obsługa wyjątków: własne klasy wyjątków

**Cel:** Napisać własne wyjątki i używać ich do sygnalizowania błędów domenowych. Opanować `try-catch-finally` i `throws`.

**Teoria w pigułce:**
Wyjątek = sygnał że coś poszło nie tak. `RuntimeException` — unchecked, nie trzeba deklarować w `throws`. `Exception` — checked, trzeba obsłużyć lub zadeklarować. `finally` wykonuje się zawsze (np. zamknięcie zasobu). `throw` rzuca wyjątek. `throws` w sygnaturze deklaruje że metoda może rzucić checked exception.

**Klasy i API, których będziesz używać:**
- `RuntimeException` — po tym dziedziczy Twój `ProductNotFoundException`
- `IllegalArgumentException` — dla błędnych danych wejściowych
- `IllegalStateException` — dla niedozwolonych operacji na obiekcie
- `try-catch-finally`
- `Exception.getMessage()`, `Exception.getCause()`

### Zadanie

Utwórz pakiet `com.example.javashop.exception` z następującymi klasami:

**`ProductNotFoundException.java`** — extends `RuntimeException`:
- konstruktor przyjmujący `int id`, komunikat: `"Produkt o ID " + id + " nie istnieje."`

**`OutOfStockException.java`** — extends `RuntimeException`:
- konstruktor przyjmujący `String productName` i `int requested`, komunikat informujący o braku towaru

**`InvalidPriceException.java`** — extends `IllegalArgumentException`:
- konstruktor przyjmujący `double price`, komunikat informujący że cena jest nieprawidłowa

Zaktualizuj `Inventory`:
- `findById` niech rzuca `ProductNotFoundException` zamiast zwracać `Optional` (możesz mieć obie wersje!)
- dodaj metodę `decreaseStock(int id, int amount)` — zmniejsza ilość. Rzuca `OutOfStockException` jeśli `amount > quantity`.

W `Main` obuduj wywołania `try-catch` i wypisz komunikaty błędów.

### Krok po kroku

1. Napisz wszystkie trzy klasy wyjątków. Skompiluj.
2. W `Inventory.decreaseStock` rzuć `OutOfStockException`. Wywołaj tę metodę z `Main` z za dużą ilością.
3. Dodaj `try-catch` w `Main`. Wypisz `e.getMessage()` w `catch`.
4. Zagnieździj `try-catch`: co jeśli najpierw `findById` rzuci wyjątek i nigdy nie dojdziemy do `decreaseStock`?
5. Dodaj blok `finally` który zawsze wypisuje `"Operacja zakończona."` — niezależnie od wyniku.

### Pytania do przemyślenia

1. Dlaczego `ProductNotFoundException` dziedziczy po `RuntimeException`, a nie `Exception`? Kiedy wybrałbyś `Exception`?
2. Co to „checked exception"? Podaj przykład z biblioteki standardowej Javy.
3. Czy w `catch` można rzucić inny wyjątek? Kiedy to ma sens?

---

## Część 7 — Zapis i odczyt danych: `FileManager` (I/O)

**Cel:** Zapisać stan magazynu do pliku tekstowego i wczytać go z powrotem. Zrozumieć strumienie i obsługę zasobów.

**Teoria w pigułce:**
Strumienie (`Stream`) to przepływ danych: do czytania (`InputStream`, `Reader`) lub zapisu (`OutputStream`, `Writer`). `BufferedReader/Writer` są szybsze bo buforują. `try-with-resources` automatycznie zamyka zasoby. Format CSV (wartości rozdzielone przecinkiem) jest najprostszy do zapisu/odczytu.

**Klasy i API, których będziesz używać:**
- `BufferedWriter`, `FileWriter` — zapis do pliku
- `BufferedReader`, `FileReader` — odczyt z pliku
- `try-with-resources` (`try (BufferedWriter bw = ...)`) — automatyczne zamykanie
- `String.split(",")` — parsowanie CSV
- `Integer.parseInt()`, `Double.parseDouble()` — konwersja stringów
- `File` — sprawdzanie czy plik istnieje, tworzenie ścieżki
- `IOException` — obsługa błędów I/O

### Zadanie

Utwórz plik: `src/main/java/com/example/javashop/util/FileManager.java`

**`FileManager.java`:**
- `saveInventory(Inventory inventory, String filePath)` — zapisuje wszystkie produkty do pliku CSV (throws `IOException`)
  Format każdej linii: `id,name,price,quantity,type` gdzie `type` to `"ELECTRONICS"` lub `"FOOD"` lub `"BASIC"`
- `loadInventory(String filePath)` — wczytuje plik CSV i zwraca `Inventory` (throws `IOException`)
  Każdą linię parsuj przez `split(",")` i twórz odpowiedni obiekt klasy `Product`

### Krok po kroku

1. Napisz `saveInventory`. Stwórz kilka produktów w `Main`, wywołaj zapis. Otwórz plik w edytorze — czy wygląda jak CSV?
2. Napisz `loadInventory`. Wczytaj plik który właśnie zapisałeś i wypisz produkty na konsolę.
3. Co się stanie jeśli plik nie istnieje podczas wczytywania? Obsłuż ten przypadek.
4. Dodaj obsługę `Electronics` — zapisz też `warrantyMonths` (rozszerz format CSV o piątą kolumnę).
5. Przetestuj pełny cykl: stwórz → zapisz → zmodyfikuj → wczytaj → porównaj.

### Pytania do przemyślenia

1. Dlaczego `try-with-resources` jest lepsze od ręcznego `finally { bw.close(); }`?
2. Co się stanie jeśli nazwa produktu zawiera przecinek? Jak rozwiązujesz ten problem w CSV?
3. Czym różni się `FileWriter(path, true)` od `FileWriter(path, false)`?

---

## Część 8 — Wielowątkowość: `StockMonitor` (Multithreading)

**Cel:** Uruchomić zadanie w tle — wątek sprawdzający co kilka sekund stan magazynu i alarmujący o niskim stanie. Zrozumieć `Thread`, `Runnable`, `synchronized`.

**Teoria w pigułce:**
`Thread` = osobny wątek wykonania. `Runnable` = zadanie do wykonania w wątku (interfejs funkcyjny z metodą `run()`). Wątki działają współbieżnie — mogą jednocześnie czytać/pisać te same dane, co może powodować błędy. `synchronized` blokuje dostęp do metody/bloku dla innych wątków. `sleep(ms)` usypia wątek na podany czas. `daemon` thread = wątek który kończy się automatycznie gdy kończy się główny wątek.

**Klasy i API, których będziesz używać:**
- `Thread` — tworzenie i uruchamianie wątku
- `Runnable` — zadanie jako lambda
- `Thread.sleep(ms)` — pauzowanie wątku
- `thread.setDaemon(true)` — ustawienie jako wątek demon
- `synchronized` — blokowanie dostępu
- `volatile` — widoczność zmiennej między wątkami
- `AtomicBoolean` (`java.util.concurrent.atomic`) — bezpieczne zatrzymanie wątku

### Zadanie

Utwórz plik: `src/main/java/com/example/javashop/monitor/StockMonitor.java`

**`StockMonitor.java`:**
- pole `inventory` (`Inventory`) — magazyn do monitorowania
- pole `threshold` (`int`) — próg niskiego stanu
- pole `running` jako `AtomicBoolean` — flaga czy monitor działa
- pole `intervalSeconds` (`int`) — co ile sekund sprawdzać
- `start()` — uruchamia wątek (ustaw go jako daemon!). Wątek w pętli `while(running.get())`:
    1. Pobiera `getLowStockProducts(threshold)` z `inventory`
    2. Jeśli lista nie jest pusta, wypisuje ostrzeżenie dla każdego produktu
    3. Czeka `intervalSeconds` sekund przez `Thread.sleep(...)`
- `stop()` — ustawia `running = false`

W `Main` uruchom `StockMonitor` w tle i dodaj kilka produktów z małą ilością. Niech główny wątek poczeka kilka sekund (`Thread.sleep(5000)`) żebyś zobaczył wyniki.

### Krok po kroku

1. Napisz `StockMonitor`. Uruchom `start()` w `Main`. Poczekaj 5 sekund. Czy pojawiają się alerty?
2. W `Main` w pętli co 2 sekundy zmniejszaj losowo `quantity` jednego z produktów. Obserwuj alerty.
3. Co się stanie jeśli nie ustawisz wątku jako `daemon`? Spróbuj — program się nie zakończy!
4. Dodaj `synchronized` do metody `getLowStockProducts` w `Inventory`. Kiedy to jest potrzebne?
5. Wywołaj `stop()` po 10 sekundach i sprawdź czy wątek się kończy.

### Pytania do przemyślenia

1. Dlaczego używamy `AtomicBoolean` zamiast zwykłego `boolean running`?
2. Co to „race condition"? Podaj przykład który mógłby wystąpić w tym kodzie.
3. Czym różni się wątek `daemon` od zwykłego wątku?

---

## Część 9 — Interfejs użytkownika: pętla główna `Main` z `Scanner`

**Cel:** Połączyć wszystkie części w działającą aplikację konsolową z menu.

**Teoria w pigułce:**
`Scanner` wczytuje dane z konsoli. `System.in` to standardowy strumień wejściowy. Główna pętla aplikacji to wzorzec REPL (Read-Eval-Print Loop) — czytaj wejście, wykonaj, wypisz wynik, powtórz. `switch` na komendach textowych jest tu czytelniejszy niż `if-else`.

**Klasy i API, których będziesz używać:**
- `Scanner(System.in)` — wczytywanie z konsoli
- `scanner.nextLine()`, `scanner.nextInt()`, `scanner.nextDouble()`
- `switch` z `String` (dostępny od Java 7)
- `System.out.printf()` — formatowanie tabeli w konsoli
- `String.trim()` — usuwanie białych znaków

### Zadanie

W `Main.java` napisz pełną pętlę aplikacji z menu:

```
=== JavaShop ===
1. Wypisz wszystkie produkty
2. Dodaj produkt
3. Dodaj do koszyka
4. Wypisz koszyk i sumę
5. Złóż zamówienie (wyczyść koszyk, zmniejsz magazyn)
6. Znajdź produkt po nazwie
7. Zapisz magazyn do pliku
8. Wczytaj magazyn z pliku
9. Uruchom/zatrzymaj monitor magazynu
0. Wyjście
```

Każda opcja wywołuje odpowiednią metodę z klas które napisałeś wcześniej.

### Krok po kroku

1. Napisz szkielet pętli `while(true)` z `switch`. Na razie każda opcja niech wypisuje `"TODO"`.
2. Zaimplementuj opcję 1 i 2. Przetestuj.
3. Zaimplementuj opcję 3, 4, 5 — koszyk. Zwróć uwagę: składając zamówienie zmniejsz ilość w magazynie (`decreaseStock`). Obsłuż `OutOfStockException`.
4. Zaimplementuj 6, 7, 8.
5. Zaimplementuj 9 — toggle monitora (jeśli działa, zatrzymaj; jeśli nie, uruchom).

### Pytania do przemyślenia

1. Co się stanie jeśli użytkownik wpisze literę zamiast cyfry przy pytaniu `scanner.nextInt()`? Jak to obsłużyć?
2. Czy `Main` powinna zawierać logikę biznesową, czy tylko wywoływać metody z innych klas?
3. Jak mógłbyś zastąpić `switch` wzorcem `Command` (każda komenda jako obiekt)?

---

## Mini-projekt końcowy — Zamówienia z historią

**Cel:** Dodać klasę `Order` i `OrderHistory`, która przechowuje złożone zamówienia — jako powtórzenie całości.

Bez wskazówek tym razem. Sam zdecyduj:
- Jakie pola powinna mieć klasa `Order`?
- Czy `Order` powinien być `Record` czy klasą?
- Jak przechowywać historię — `ArrayList`? `HashMap` po dacie?
- Jak zapisać historię do pliku?
- Jak wypisać historię w konsoli?

Powodzenia. 🎯
