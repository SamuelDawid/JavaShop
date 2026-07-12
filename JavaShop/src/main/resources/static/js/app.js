// ---- Konfiguracja ----
const API_KEY = "let-me-in";           // ten sam klucz, którego pilnuje ApiKeyFilter

// Placeholder obrazka (ten sam dla wszystkich produktów na razie)
const IMAGE_ICON = `
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"
       stroke-linecap="round" stroke-linejoin="round">
    <rect x="3" y="3" width="18" height="18" rx="2"/>
    <circle cx="8.5" cy="8.5" r="1.5"/>
    <path d="M21 15l-5-5L5 21"/>
  </svg>`;

// ---- Pobranie i render produktów ----
async function loadProducts() {
    const grid = document.getElementById("product-grid");
    const status = document.getElementById("status");

    status.textContent = "Ładowanie produktów…";
    grid.innerHTML = "";

    try {
        const res = await fetch("/api/products", {
            headers: { "X-API-KEY": API_KEY }
        });

        if (!res.ok) throw new Error("HTTP " + res.status);

        const products = await res.json();

        if (products.length === 0) {
            status.textContent = "Brak produktów.";
            return;
        }

        status.textContent = "";
        products.forEach(p => grid.appendChild(createCard(p)));

    } catch (err) {
        status.textContent = "Nie udało się załadować produktów: " + err.message;
    }
}

// ---- Budowa karty produktu ----
function createCard(product) {
    const card = document.createElement("article");
    card.className = "card";

    const price = Number(product.price).toFixed(2);

    card.innerHTML = `
        <div class="card-img" aria-hidden="true">${IMAGE_ICON}</div>
        <div class="card-body">
            <h2 class="card-name"></h2>
            <div class="card-meta">
                <span class="price">${price} zł</span>
                <span class="qty">Dostępne: ${product.quantity}</span>
            </div>
            <button class="btn-add">Dodaj do koszyka</button>
        </div>`;

    // nazwa przez textContent — bezpiecznie, bez wstrzykiwania HTML
    card.querySelector(".card-name").textContent = product.name;

    const btn = card.querySelector(".btn-add");
    if (product.quantity <= 0) {
        btn.disabled = true;
        btn.textContent = "Niedostępny";
    } else {
        btn.addEventListener("click", () => addToCart(product));
    }

    return card;
}

// ---- Dodaj do koszyka (na razie zaślepka) ----
function addToCart(product) {
    console.log("Dodano do koszyka:", product.id);
    // Logikę koszyka dodamy w kolejnym kroku.
    alert(`„${product.name}" — obsługę koszyka dodamy wkrótce.`);
}

// ---- Przełączanie widoków (navbar) ----
function showView(view) {
    document.querySelectorAll(".view").forEach(v => v.classList.remove("active"));
    document.getElementById("view-" + view).classList.add("active");

    document.querySelectorAll(".nav-link").forEach(link => {
        link.classList.toggle("active", link.dataset.view === view);
    });

    if (view === "home") loadProducts();
}

// ---- Start ----
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("[data-view]").forEach(el => {
        el.addEventListener("click", e => {
            e.preventDefault();
            showView(el.dataset.view);
        });
    });

    loadProducts();   // pokaż listę produktów od razu po wejściu
});
