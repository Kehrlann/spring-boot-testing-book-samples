async function loadWeather() {
  const response = await fetch("/api/weather");
  return await response.json();
}

function renderCity(cityWeather) {
  return `
        <div class="card">
            <h5 class="card-title">${cityWeather.cityName} (${cityWeather.country})</h5>
            <p>
                Temperature: <span>${cityWeather.temperature}</span>°C<br>
                Wind Speed: <span>${cityWeather.windSpeed}</span> km/h<br>
                Weather: <span>${cityWeather.weather}</span>
            </p>
            <form data-role="delete-city">
                <input type="hidden" name="cityId" value="${cityWeather.cityId}">
                <button type="submit" class="button button-danger button-sm">Remove</button>
            </form>
        </div>
    `;
}

async function refreshCities() {
  const cities = await loadWeather();
  const citiesGrid = document.querySelector(".cities-grid");
  citiesGrid.innerHTML = cities.map(renderCity).join("\n");
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
    selectCityFunction,
  ) {
    this.input = inputElement;
    this.results = resultsElement;
    this.searchFunction = searchFunction;
    this.selectedIndex = -1;
    this.selectCityFunction = selectCityFunction;
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
      true,
    );
  }

  async handleInput(e) {
    const query = e.target.value.trim();
    if (query.length >= 2) {
      const cities = await this.searchFunction(query);
      this.results.innerHTML = cities
        .map(
          (city) =>
            `<div class="autocomplete-item" data-id="${city.id}">
                    ${city.name} (${city.country})
                </div>`,
        )
        .join("");
      this.selectedIndex = -1;
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

document.addEventListener("DOMContentLoaded", () => {
  const citySearch = document.getElementById("citySearch");
  const cityResults = document.getElementById("cityResults");

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
    },
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
});
