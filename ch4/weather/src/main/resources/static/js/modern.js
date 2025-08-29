/**
 * @file A "modern" (ECMAScript 2017) implementation of our "weather application".
 * It has _fancy_ stuff in it, like async-await! :gasp:
 *
 * Please note: this is NOT good JavaScript. I know enough to be dangerous, and with
 * the help of an LLM, I can cobble something together. This does not mean you
 * should listen to me for JS advice, ever.
 * HIC SUNT DRACONES.
 */

async function loadWeather() {
  const response = await fetch("/api/weather");
  return await response.json();
}

function renderCity(cityWeather, isCompact = false) {
  return `
        <div class="card">
          <div class="compact-display ${isCompact ? "" : "hidden"}">
            <span style="font-weight: bold;">${cityWeather.cityName} (${cityWeather.country})</span>
            <span>${cityWeather.temperature}°C</span>
            <form data-role="delete-city" style="margin: 0;">
              <input type="hidden" name="cityId" value="${cityWeather.cityId}">
              <button type="submit" class="button button-danger button-sm">&#x2718;</button>
            </form>
          </div>
          <div class="full-display  ${isCompact ? "hidden" : ""}">
            <h5 class="card-title">${cityWeather.cityName} (${cityWeather.country})</h5>
            <p class="card-content">
                Temperature: <span>${cityWeather.temperature}</span>°C<br>
                Wind Speed: <span>${cityWeather.windSpeed}</span> km/h<br>
                Weather: <span>${cityWeather.weather}</span>
            </p>
            <form data-role="delete-city">
                <input type="hidden" name="cityId" value="${cityWeather.cityId}">
                <button type="submit" class="button button-danger button-sm">Remove</button>
            </form>
          </div>
        </div>
    `;
}

let isCompactMode = false;
let currentCities = null;

function getDisplayModeFromURL() {
  const urlParams = new URLSearchParams(window.location.search);
  const display = urlParams.get("display");
  return display === "compact";
}

function updateURLWithDisplayMode(mode) {
  const url = new URL(window.location);
  url.searchParams.set("display", mode);
  window.history.replaceState({}, "", url);
}

async function refreshCities() {
  const cities = await loadWeather();
  currentCities = cities;
  await rerenderCities();
}

async function rerenderCities() {
  if (currentCities != null) {
    const citiesGrid = document.querySelector(".cities-grid");
    citiesGrid.innerHTML = currentCities
      .map((city) => renderCity(city, isCompactMode))
      .join("\n");
  } else {
    await refreshCities();
  }
}

function setDisplayMode(compact) {
  isCompactMode = compact;
  document.querySelectorAll(".card > .full-display").forEach((c) => {
    if (!compact) {
      c.classList.remove("hidden");
    } else if (!c.classList.contains("hidden")) {
      c.classList.add("hidden");
    }
  });
  document.querySelectorAll(".card > .compact-display").forEach((c) => {
    if (compact) {
      c.classList.remove("hidden");
    } else if (!c.classList.contains("hidden")) {
      c.classList.add("hidden");
    }
  });
}

async function addCity(cityId) {
  const response = await fetch("/api/city", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ id: cityId }),
  });
  return response.ok;
}

async function removeCity(cityId) {
  const response = await fetch("/api/city", {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ id: cityId }),
  });
  return response.ok;
}

async function searchCities(query) {
  const response = await fetch(`/api/city?q=${encodeURIComponent(query)}`);
  return await response.json();
}

class AutocompleteDropdown {
  constructor(
    inputElement,
    resultsElement,
    searchFunction,
    selectCityFunction
  ) {
    this.input = inputElement;
    this.results = resultsElement;
    this.searchFunction = searchFunction;
    this.selectedIndex = -1;
    this.selectCityFunction = selectCityFunction;
    this.currentSearchRequestId = 0;
    this.setupEventListeners();
  }

  setupEventListeners() {
    this.input.addEventListener("input", this.handleInput.bind(this));
    this.input.addEventListener("keydown", this.handleKeydown.bind(this));
    this.input.addEventListener("blur", this.handleBlur.bind(this));
    this.results.addEventListener("click", this.handleClick.bind(this));
    this.results.addEventListener(
      "mouseenter",
      this.handleMouseEnter.bind(this),
      true
    );
  }

  async handleInput(e) {
    const query = e.target.value.trim();
    if (query.length >= 2) {
      const requestId = ++this.currentSearchRequestId;
      const cities = await this.searchFunction(query);
      if (requestId !== this.currentSearchRequestId) {
        return;
      }
      this.results.innerHTML = cities
        .map(
          (city) =>
            `<div class="autocomplete-item" data-id="${city.id}">
                    ${city.name} (${city.country})
                </div>`
        )
        .join("");
      this.selectedIndex = cities.length === 1 ? 0 : -1;
      this.updateSelection();
    } else {
      this.hide();
    }
  }

  async handleKeydown(e) {
    const items = this.results.querySelectorAll(".autocomplete-item");

    if (items.length === 0) return;

    switch (e.key) {
      case "ArrowDown":
        e.preventDefault();
        this.selectedIndex = Math.min(this.selectedIndex + 1, items.length - 1);
        this.updateSelection();
        break;
      case "ArrowUp":
        e.preventDefault();
        this.selectedIndex = Math.max(this.selectedIndex - 1, 0);
        this.updateSelection();
        break;
      case "Enter":
        e.preventDefault();
        if (this.selectedIndex >= 0 && items[this.selectedIndex]) {
          await this.selectItem(items[this.selectedIndex]);
        }
        break;
      case "Escape":
        e.preventDefault();
        this.reset();
        break;
    }
  }

  handleBlur() {
    setTimeout(() => {
      if (!this.results.matches(":hover")) {
        this.hide();
      }
    }, 150);
  }

  async handleClick(e) {
    if (e.target.classList.contains("autocomplete-item")) {
      await this.selectItem(e.target);
    }
  }

  handleMouseEnter(e) {
    if (e.target.classList.contains("autocomplete-item")) {
      const items = this.results.querySelectorAll(".autocomplete-item");
      this.selectedIndex = Array.from(items).indexOf(e.target);
      this.updateSelection();
    }
  }

  async selectItem(item) {
    const selectedCityId = parseInt(item.dataset.id);
    await this.selectCityFunction(selectedCityId);
    this.reset();
  }

  updateSelection() {
    const items = this.results.querySelectorAll(".autocomplete-item");

    items.forEach((item, index) => {
      item.classList.toggle("selected", index === this.selectedIndex);
    });

    // Auto-scroll to keep selected item in view
    if (this.selectedIndex >= 0 && items[this.selectedIndex]) {
      const selectedItem = items[this.selectedIndex];
      const containerHeight = this.results.clientHeight;
      const itemHeight = selectedItem.offsetHeight;
      const itemTop = selectedItem.offsetTop;
      const itemBottom = itemTop + itemHeight;
      const scrollTop = this.results.scrollTop;
      const scrollBottom = scrollTop + containerHeight;

      if (itemTop < scrollTop) {
        this.results.scrollTop = itemTop;
      } else if (itemBottom > scrollBottom) {
        this.results.scrollTop = itemBottom - containerHeight;
      }
    }
  }

  hide() {
    this.results.innerHTML = "";
    this.selectedIndex = -1;
  }

  reset() {
    this.selectedIndex = -1;
    this.input.value = "";
    this.hide();
  }
}

document.addEventListener("DOMContentLoaded", async () => {
  const citySearch = document.getElementById("citySearch");
  const cityResults = document.getElementById("cityResults");

  // Initialize display mode from URL
  isCompactMode = getDisplayModeFromURL();
  setDisplayMode(isCompactMode);

  // Initialize autocomplete dropdown
  const autocomplete = new AutocompleteDropdown(
    citySearch,
    cityResults,
    searchCities,
    async (id) => {
      const success = await addCity(id);
      if (success) {
        await refreshCities();
      }
    }
  );

  const citiesGrid = document.querySelector(".cities-grid");
  citiesGrid.addEventListener("submit", async (e) => {
    if (e.target.matches('form[data-role="delete-city"]')) {
      e.preventDefault();
      const formData = new FormData(e.target);
      const cityId = parseInt(formData.get("cityId"));
      if (cityId) {
        const success = await removeCity(cityId);
        if (success) {
          await refreshCities();
        }
      }
    }
  });

  // Initialize display toggle buttons
  const fullButton = document.getElementById("button-display-full");
  const compactButton = document.getElementById("button-display-compact");

  // Set initial button states based on URL
  if (isCompactMode) {
    compactButton.classList.add("button-primary");
    fullButton.classList.remove("button-primary");
  } else {
    fullButton.classList.add("button-primary");
    compactButton.classList.remove("button-primary");
  }

  fullButton.addEventListener("click", () => {
    fullButton.classList.add("button-primary");
    compactButton.classList.remove("button-primary");
    updateURLWithDisplayMode("full");
    setDisplayMode(false);
  });

  compactButton.addEventListener("click", () => {
    compactButton.classList.add("button-primary");
    fullButton.classList.remove("button-primary");
    updateURLWithDisplayMode("compact");
    setDisplayMode(true);
  });
});
