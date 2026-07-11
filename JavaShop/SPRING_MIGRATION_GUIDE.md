# JavaShop — Migrate Console → Real Spring Boot Web App

**This replaces the old `DATABASE_TUTORIAL.md` (raw JDBC + hand-written SQL). Delete that file — you won't hand-write schemas anymore.**

## What you're building

A proper Spring Boot web application:

- **PostgreSQL running in Docker** (via `docker-compose`), the way a real project does it
- **Spring Data JPA** — your entity classes define the tables; Hibernate creates them. No `schema.sql`.
- **Real dependency injection** — Spring builds all your beans; you delete the manual `new` graph in `App.java`
- **REST API** — `@RestController` endpoints returning JSON
- **Jackson `ObjectMapper`** — objects ↔ JSON, mostly automatic
- **One auth `Filter`** — a servlet filter that checks an API key on every request
- **A tiny frontend** — static HTML + `fetch()` so you can watch HTTP happen in the browser

By the end you'll `docker compose up`, run the app, open `http://localhost:8080`, and hit real endpoints.

## Mental model (this fixes your confusion)

- **You stopped needing SQL schemas.** With JPA you annotate `Electronics` as `@Entity` and set `ddl-auto`, and the `products` table is created for you.
- **Docker runs the database server (Postgres), not your app.** Your Spring app runs on your machine (or later, its own container) and *connects* to the Postgres container over port 5432.
- **`new` disappears.** Right now `App.main` manually wires everything. In real Spring, you annotate classes (`@Service`, `@RestController`, `@Repository`) and Spring injects them through constructors — which you already do in `ProductManager`.

Clean up the earlier detour first:

```bash
docker stop my-h2 && docker rm my-h2
```

---

## Prerequisites

- Docker Desktop installed and running
- JDK 17+ and Maven (you have these)
- A REST client to test: browser, `curl`, or Postman

---

# Phase 1 — Dependencies (`pom.xml`)

Add these inside the existing `<dependencies>` block. Versions are managed by the Spring Boot parent, so most need no `<version>`.

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <scope>runtime</scope>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

- `starter-web` — brings in Spring MVC + an embedded Tomcat server + Jackson (that's your `ObjectMapper`).
- `starter-data-jpa` — Spring Data + Hibernate.
- `postgresql` — the JDBC driver, only needed at runtime.
- `starter-validation` — for `@Valid` on request bodies later.

Reload Maven after saving.

---

# Phase 2 — PostgreSQL in Docker

Create `docker-compose.yml` in the project root (next to `pom.xml`):

```yaml
services:
  db:
    image: postgres:16
    container_name: javashop-db
    environment:
      POSTGRES_DB: javashop
      POSTGRES_USER: javashop
      POSTGRES_PASSWORD: javashop
    ports:
      - "5432:5432"
    volumes:
      - javashop-data:/var/lib/postgresql/data

volumes:
  javashop-data:
```

Start it:

```bash
docker compose up -d
```

`-d` runs it in the background. Check it's healthy:

```bash
docker compose ps
docker compose logs db | tail -20    # look for "database system is ready to accept connections"
```

- The named volume `javashop-data` means your data survives `docker compose down` and restarts.
- Port `5432` is Postgres's standard port — remember your HTTP quiz: the port picks the service on the host. If something else already uses 5432, change the left number (e.g. `"5433:5432"`) and update the URL in Phase 3.

To connect **DataGrip** here later: new Data Source → **PostgreSQL**, host `localhost`, port `5432`, db/user/password all `javashop`.

---

# Phase 3 — Configuration

Delete any `schema.sql` you made. Create `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/javashop
spring.datasource.username=javashop
spring.datasource.password=javashop

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.port=8080
```

- `ddl-auto=update` — Hibernate creates/updates tables from your `@Entity` classes on startup. (For learning this is fine; production uses migrations like Flyway — a later topic.)
- `show-sql` + `format_sql` — prints the SQL Hibernate runs, so you can *see* JPA turning objects into SQL. Great for learning.

---

# Phase 4 — Make `App` an actual web app

Right now `App.main` builds objects by hand and starts a `ShopCLI`. Replace the whole body of `main` with the one line that boots Spring:

```java
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```

Import `org.springframework.boot.SpringApplication`. That's it — Spring now scans your packages, creates every `@Service`/`@Repository`/`@RestController`/`@Component` bean, and starts Tomcat on 8080.

**Your seed data** (the products in the old `main`) moves into a bean that runs on startup. Create `config/DataSeeder.java`:

```java
@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductManager productManager;

    public DataSeeder(ProductManager productManager) {
        this.productManager = productManager;   // Spring injects it
    }

    @Override
    public void run(String... args) {
        // your turn: add the sample Computers/SmartPhones here,
        // but only if the DB is empty (check productManager.returnAllProducts()).
        // Move the List.of(new Computer(...), new SmartPhone(...)) block here.
    }
}
```

Notice: no `new ProductManager(...)`. You *ask* for it via the constructor and Spring hands you the one it built. That's DI — the thing you wanted to practise.

The old CLI (`ShopCLI`) can stay in the project for now, just not called from `main`. You're moving from CLI to HTTP.

---

# Phase 5 — Turn `Electronics` into a JPA entity

This is the fiddly part because you have inheritance (`Computer`/`SmartPhone extends Electronics`) and Lombok. Do it carefully.

On `Electronics`:

```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "product_type")
@Getter
public class Electronics {

    @Id
    private String id;

    private String name;

    @Setter
    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    @Setter
    private int quantity;

    protected Electronics() { }   // JPA REQUIRES a no-arg constructor

    // keep your existing validating constructor exactly as it is
}
```

Then on the subclasses:

```java
@Entity
@DiscriminatorValue("COMPUTER")
public class Computer extends Electronics { ... }

@Entity
@DiscriminatorValue("PHONE")
public class SmartPhone extends Electronics { ... }
```

Field-by-field, what each subclass field needs (your turn to add the annotations):

| Field | Annotation | Why |
|---|---|---|
| `cpu`, `gpu`, `ram` (enums) | `@Enumerated(EnumType.STRING)` | store the enum *name*, not its ordinal number |
| `battery`, `colour` (enums) | `@Enumerated(EnumType.STRING)` | same |
| `accessories` (`List<ACCESSORIES>`) | `@ElementCollection` + `@Enumerated(EnumType.STRING)` | a collection of values → its own little table |

**Get-it-running shortcut:** if the `accessories` list gives you trouble, temporarily put `@Transient` on it (JPA ignores it) so the rest works, then come back and do `@ElementCollection` properly. Ship the skeleton first, deepen after.

Why `SINGLE_TABLE`: all products live in one `electronics` table with a `product_type` column telling Computer rows from Phone rows. It's the simplest inheritance strategy — a good first choice.

Guiding question: why does JPA need a no-arg constructor when your app code always uses the validating one? (Hint: how does Hibernate create an object *before* it has the values to pass in?)

---

# Phase 6 — Repositories become Spring Data JPA

You can now **delete** `InMemoryProductRepository` (or keep it as reference). Replace your `ProductsRepository` interface with a Spring Data one:

```java
public interface ProductsRepository extends JpaRepository<Electronics, String> {

    // findById, findAll, save, deleteById, existsById — all FREE, no code

    // custom stock decrement as a single atomic UPDATE:
    @Modifying
    @Query("update Electronics e set e.quantity = e.quantity - :qty " +
           "where e.id = :id and e.quantity >= :qty")
    int decreaseStock(@Param("id") String id, @Param("qty") int qty);
}
```

`JpaRepository<Electronics, String>` = "entity is `Electronics`, its id type is `String`." You inherit full CRUD for free — this is the payoff versus the hand-written JDBC you were doing.

**`ProductManager` needs small edits** because the JPA method names differ from your old interface. Map them yourself:

| Your old call | Spring Data equivalent |
|---|---|
| `repo.save(p)` returning boolean | `repo.existsById(id)` check, then `repo.save(p)` (returns the entity) |
| `repo.findById(id)` | `repo.findById(id)` — same, returns `Optional` |
| `repo.findAll()` | `repo.findAll()` — same, returns `List` |
| `repo.update(id, p)` | `repo.save(p)` (save = insert or update by id) |
| `repo.delete(id)` returning boolean | `repo.existsById(id)` then `repo.deleteById(id)` |
| `repo.decreaseStock(id, qty)` | the `@Modifying` query above (add `@Transactional` on the calling service method) |

Do the same for accounts later if you want, but **accounts are optional** — you said you don't need them. Focus on products for the web slice.

---

# Phase 7 — Delete the manual wiring

Anything in the old `App.main` that did `new PaymentService(...)`, `new OrderProcessor(...)`, etc. is now Spring's job.

- Classes with `@Service`/`@Component`/`@RestController` are auto-created.
- For objects built from a `Map` or `List` (like `PaymentService`'s payment-methods map and validators list), create a `@Configuration` class with `@Bean` methods that build and return them:

```java
@Configuration
public class ShopConfig {

    @Bean
    public PaymentService paymentService(/* inject what it needs */) {
        // build the methods map + validators list here and return new PaymentService(...)
    }
}
```

A `@Bean` method is where you're *allowed* to `new` something — Spring runs it once and manages the result. Your turn to fill it in from the old `main`.

---

# Phase 8 — Your first REST controller

Create `web/ProductController.java`. **Worked example** — study this GET, it's your template:

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductManager productManager;

    public ProductController(ProductManager productManager) {
        this.productManager = productManager;   // DI again
    }

    @GetMapping
    public List<Electronics> all() {
        return productManager.findAll();   // Jackson turns this List into JSON automatically
    }

    @GetMapping("/{id}")
    public ResponseEntity<Electronics> byId(@PathVariable String id) {
        return productManager.findById(id)
                .map(ResponseEntity::ok)                    // 200 + body
                .orElse(ResponseEntity.notFound().build()); // 404
    }
}
```

See how the status codes from your HTTP quiz show up: `200` when found, `404` when not. Jackson (the `ObjectMapper`) serialises the returned object to JSON — you didn't write any JSON code.

**Your turn** — add the write endpoints (signatures + hints, no bodies):

```java
@PostMapping
public ResponseEntity<Electronics> create(@RequestBody ProductDto dto) { }
// map dto -> Electronics, save it, return 201 Created
// hint: ResponseEntity.status(HttpStatus.CREATED).body(saved)

@PutMapping("/{id}")
public ResponseEntity<Electronics> update(@PathVariable String id, @RequestBody ProductDto dto) { }
// if id exists -> update and return 200, else 404

@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable String id) { }
// delete, return 204 No Content on success, 404 if it wasn't there
```

`@RequestBody` is Jackson going the *other* direction — JSON from the client into a Java object.

---

# Phase 9 — DTOs and the ObjectMapper

Returning entities directly is fine to start, but the "normal" pattern is a **DTO** (data transfer object) so your API shape is separate from your DB shape. Create `web/ProductDto.java`:

```java
public record ProductDto(String id, String name, BigDecimal price, int quantity) { }
```

A `record` is perfect for DTOs. Jackson maps JSON ↔ record automatically.

To *see* the `ObjectMapper` explicitly (you asked to implement it), inject Spring's one and use it in a tiny debug endpoint:

```java
private final ObjectMapper objectMapper;   // Spring already made this bean; inject it

// in some method:
// String json = objectMapper.writeValueAsString(product);   // object -> JSON string
// Electronics p = objectMapper.readValue(json, Electronics.class); // JSON -> object
```

That's literally what Spring runs under the hood for every request/response. Writing it once by hand makes the "magic" concrete.

---

# Phase 10 — One auth filter

Create `security/ApiKeyFilter.java`. A servlet filter runs **before** your controllers on every request — exactly the Servlet-layer concept from your roadmap.

```java
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-API-KEY";
    private static final String EXPECTED = "let-me-in";   // hardcoded for learning only

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        // your turn:
        // 1. read request.getHeader(HEADER)
        // 2. if it equals EXPECTED -> chain.doFilter(request, response) (let it through)
        // 3. else -> response.setStatus(401) and write a short message, then RETURN (don't call chain)
    }
}
```

Test it: a request without `X-API-KEY: let-me-in` should get **401 Unauthorized**; with it, **200**. That's the whole point of a filter — a cross-cutting check in one place, not repeated in every controller.

Tip: while building, you may want to exempt `/` and `/index.html` so your frontend loads — add an early `if (request.getRequestURI().equals("/")) { chain.doFilter(...); return; }`.

---

# Phase 11 — A tiny frontend

Create `src/main/resources/static/index.html`. Spring serves anything in `static/` at the root automatically. This is a harness to watch HTTP in your browser's Network tab:

```html
<!DOCTYPE html>
<html>
<head><meta charset="utf-8"><title>JavaShop</title></head>
<body>
  <h1>Products</h1>
  <button onclick="load()">Load products</button>
  <ul id="list"></ul>

  <script>
    async function load() {
      const res = await fetch('/api/products', {
        headers: { 'X-API-KEY': 'let-me-in' }   // matches your filter
      });
      console.log('status', res.status);         // watch the status code
      const products = await res.json();          // Jackson's JSON -> JS objects
      document.getElementById('list').innerHTML =
        products.map(p => `<li>${p.name} — ${p.price} zł (${p.quantity})</li>`).join('');
    }
  </script>
</body>
</html>
```

Open `http://localhost:8080`, click the button, and open DevTools → Network. You'll *see* the GET, the `200`, the request/response headers, and the JSON body — everything from your HTTP session, live.

---

# Phase 12 — Run it and verify

```bash
docker compose up -d          # Postgres running
mvn spring-boot:run           # or run App.main from IntelliJ
```

Watch the startup log: Hibernate prints the `create table electronics ...` SQL (because of `ddl-auto` + `show-sql`). Then test:

```bash
# should be 401 (no key)
curl -i http://localhost:8080/api/products

# should be 200 + JSON
curl -i -H "X-API-KEY: let-me-in" http://localhost:8080/api/products

# create one (201)
curl -i -X POST http://localhost:8080/api/products \
  -H "X-API-KEY: let-me-in" -H "Content-Type: application/json" \
  -d '{"id":"PC-9","name":"Test PC","price":1000.00,"quantity":3}'
```

Each `curl -i` prints the status line and headers — read them and connect back to the quiz.

---

## Common pitfalls

- **`No default constructor for entity`** — you forgot the `protected Electronics() {}`. JPA needs it.
- **Enum stored as a number** — add `@Enumerated(EnumType.STRING)`; the default is the ordinal int, which breaks if you reorder the enum.
- **App can't connect to Postgres** — is the container up (`docker compose ps`)? Right port? Credentials match `application.properties`?
- **Port 8080 or 5432 already in use** — change `server.port` or the compose port mapping.
- **Filter blocks your own frontend** — exempt `/` and static paths, or send the header from the page (the example does both-friendly).
- **`ddl-auto`** — `update` is convenient but never blindly trust it in production; that's what migrations are for (later).
- **Circular/`@Transactional` on decreaseStock** — the `@Modifying` query needs a transactional boundary; annotate the calling service method `@Transactional`.

---

## How this hits every goal you listed

- **Console → Spring web app:** Phases 4, 8, 12.
- **Controllers + business logic:** Phase 8 (controllers call your existing `@Service` classes).
- **DI in Spring:** Phases 4, 7 — constructor injection, `@Bean`, no more manual `new`.
- **Beans + H2/Postgres DB:** Phases 5–6, entities as beans, JPA repositories.
- **REST API + endpoints:** Phase 8.
- **ObjectMapper:** Phase 9.
- **See how HTTP works:** Phases 11–12 (status codes, headers, JSON, DevTools).
- **One auth filter:** Phase 10.

---

## Definition of done

- [ ] `docker compose up` runs Postgres; app connects to it
- [ ] `App.main` is just `SpringApplication.run(...)`; manual `new` graph deleted
- [ ] `Electronics` (+ subclasses) are `@Entity`; tables auto-created (seen in the log)
- [ ] `ProductsRepository extends JpaRepository`; `ProductManager` refactored to it
- [ ] `GET/POST/PUT/DELETE /api/products` work with correct status codes
- [ ] `ProductDto` used for request/response; ObjectMapper understood
- [ ] `ApiKeyFilter` returns 401 without the header, 200 with it
- [ ] `index.html` lists products from the API; you watched it in DevTools

---

## Quick quiz (answer when done)

1. Where did the `new` calls go, and who creates your beans now?
2. Why does `Electronics` need a `protected` no-arg constructor?
3. Which annotation makes JSON come *into* a controller method, and which serialises the return value *out*?
4. Your filter returns 401 — at what point in the request does it run relative to the controller?
5. What does `ddl-auto=update` do, and why is it fine for learning but risky for production?

When you get a phase working (or stuck), tell me which phase and paste any error — I'll help you debug that step, and we'll turn this into your Session 02.
