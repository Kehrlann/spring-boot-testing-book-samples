/**
 * @file A "legacy" (ECMAScript 6) implementation of our "weather application". At
 * least it has Promise!
 *
 * Please note: this is NOT good JavaScript. I know enough to be dangerous, and with
 * the help of an LLM, I can cobble something together. This does not mean you
 * should listen to me for JS advice, ever.
 * HIC SUNT DRACONES. (https://en.wikipedia.org/wiki/Here_be_dragons)
 */

var currentCities = null;

function loadWeather() {
  return fetch("/api/weather").then((response) => response.json());
}

function renderCity(cityWeather) {
  const isImperial = document.getElementById("unitToggle").checked;
  let tempDisplay = cityWeather.temperature;
  let tempUnit = "°C";
  let windSpeedDisplay = cityWeather.windSpeed;
  let windSpeedUnit = "km/h";

  if (isImperial) {
    tempDisplay = Math.round(cityWeather.temperature * 9 / 5 + 32);
    tempUnit = "°F";
    windSpeedDisplay = Math.round(cityWeather.windSpeed / 1.609344);
    windSpeedUnit = "mph";
  }

  return `
        <div class="card">
          <h5 class="card-title">${cityWeather.cityName} (${cityWeather.country})</h5>
          <p class="card-content">
              Temperature: <span>${tempDisplay}</span>${tempUnit}<br>
              Wind Speed: <span>${windSpeedDisplay}</span> ${windSpeedUnit}<br>
              Weather: <span>${cityWeather.weather}</span>
          </p>
          <form data-role="delete-city">
              <input type="hidden" name="cityId" value="${cityWeather.cityId}">
              <button type="submit" class="button button-danger button-sm">Remove</button>
          </form>
        </div>
    `;
}

function refreshCities() {
  return loadWeather().then((cities) => {
    currentCities = cities;
    
    // Sort cities based on preference
    const sortBy = document.getElementById("sortSelect").value;
    if (sortBy === "ALPHABETICAL") {
      currentCities.sort((a, b) => a.cityName.localeCompare(b.cityName));
    }
    // Note: for DATE_ADDED we assume the server already returns them in that order,
    // or we leave them in the order they were fetched if no other data is available.

    var citiesGrid = document.querySelector(".cities-grid");
    citiesGrid.innerHTML = currentCities
      .map((city) => renderCity(city))
      .join("\n");
  });
}

function addCity(cityId) {
  return fetch("/api/city", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ id: cityId }),
  }).then((response) => response.ok);
}

function removeCity(cityId) {
  return fetch(`/api/city/${cityId}`, {
    method: "DELETE",
  }).then((response) => response.ok);
}

function searchCities(query) {
  return fetch(`/api/city?q=${encodeURIComponent(query)}`).then((response) =>
    response.json()
  );
}

function AutocompleteDropdown(
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

AutocompleteDropdown.prototype.setupEventListeners = function () {
  this.input.addEventListener("input", this.handleInput.bind(this));
  this.input.addEventListener("keydown", this.handleKeydown.bind(this));
  this.input.addEventListener("blur", this.handleBlur.bind(this));
  this.results.addEventListener("click", this.handleClick.bind(this));
  this.results.addEventListener(
    "mouseenter",
    this.handleMouseEnter.bind(this),
    true
  );
};

AutocompleteDropdown.prototype.handleInput = function (e) {
  const query = e.target.value.trim();
  if (query.length >= 2) {
    const requestId = ++this.currentSearchRequestId;
    this.searchFunction(query).then((cities) => {
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
    });
  } else {
    this.hide();
  }
};

AutocompleteDropdown.prototype.handleKeydown = function (e) {
  const items = this.results.querySelectorAll(".autocomplete-item");

  if (items.length === 0) return;

  switch (e.keyCode) {
    case 40: // arrow down
      e.preventDefault();
      this.selectedIndex = Math.min(this.selectedIndex + 1, items.length - 1);
      this.updateSelection();
      break;
    case 38: // arrow up
      e.preventDefault();
      this.selectedIndex = Math.max(this.selectedIndex - 1, 0);
      this.updateSelection();
      break;
    case 13: // enter
      e.preventDefault();
      if (this.selectedIndex >= 0 && items[this.selectedIndex]) {
        this.selectItem(items[this.selectedIndex]);
      }
      break;
    case 27: // escape
      e.preventDefault();
      this.reset();
      break;
  }
};

AutocompleteDropdown.prototype.handleBlur = function () {
  setTimeout(() => {
    if (!this.results.matches(":hover")) {
      this.hide();
    }
  }, 150);
};

AutocompleteDropdown.prototype.handleClick = function (e) {
  if (e.target.classList.contains("autocomplete-item")) {
    this.selectItem(e.target);
  }
};

AutocompleteDropdown.prototype.handleMouseEnter = function (e) {
  if (e.target.classList.contains("autocomplete-item")) {
    const items = this.results.querySelectorAll(".autocomplete-item");
    this.selectedIndex = Array.from(items).indexOf(e.target);
    this.updateSelection();
  }
};

AutocompleteDropdown.prototype.selectItem = function (item) {
  const selectedCityId = parseInt(item.dataset.id);
  this.selectCityFunction(selectedCityId).then(() => this.reset());
};

AutocompleteDropdown.prototype.updateSelection = function () {
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
};

AutocompleteDropdown.prototype.hide = function () {
  this.results.innerHTML = "";
  this.selectedIndex = -1;
};

AutocompleteDropdown.prototype.reset = function () {
  this.selectedIndex = -1;
  this.input.value = "";
  this.hide();
};

function applyDarkMode(isDark) {
  if (isDark) {
    document.body.classList.add("dark-mode");
  } else {
    document.body.classList.remove("dark-mode");
  }
}

function updatePreferences() {
  const darkMode = document.getElementById("darkModeToggle").checked;
  const units = document.getElementById("unitToggle").checked ? "IMPERIAL" : "METRIC";
  const sortBy = document.getElementById("sortSelect").value;

  applyDarkMode(darkMode);

  fetch("/api/preferences", {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      darkMode: darkMode,
      units: units,
      sortBy: sortBy,
    }),
  }).then((response) => {
    if (response.ok) {
      if (typeof refreshCities === "function") {
        refreshCities();
      }
    }
  });
}

function loadPreferences() {
  return fetch("/api/preferences")
    .then((response) => response.json())
    .then((preferences) => {
      document.getElementById("darkModeToggle").checked = preferences.darkMode;
      document.getElementById("unitToggle").checked = preferences.units === "IMPERIAL";
      if (preferences.sortBy) {
        document.getElementById("sortSelect").value = preferences.sortBy;
      }
      applyDarkMode(preferences.darkMode);
    });
}

document.addEventListener("DOMContentLoaded", function () {
  var citySearch = document.getElementById("citySearch");
  var cityResults = document.getElementById("cityResults");

  // Initialize autocomplete dropdown
  var autocomplete = new AutocompleteDropdown(
    citySearch,
    cityResults,
    searchCities,
    function (id) {
      return addCity(id).then(function (success) {
        if (success) {
          return refreshCities();
        }
      });
    }
  );

  var citiesGrid = document.querySelector(".cities-grid");
  citiesGrid.addEventListener("submit", function (e) {
    if (e.target.matches('form[data-role="delete-city"]')) {
      e.preventDefault();
      var formData = new FormData(e.target);
      var cityId = parseInt(formData.get("cityId"));
      if (cityId) {
        removeCity(cityId).then(function (success) {
          if (success) {
            return refreshCities();
          }
        });
      }
    }
  });

  // Preference change listeners
  document.getElementById("darkModeToggle").addEventListener("change", updatePreferences);
  document.getElementById("unitToggle").addEventListener("change", updatePreferences);
  document.getElementById("sortSelect").addEventListener("change", updatePreferences);

  // Load weather data and preferences on page load
  loadPreferences().then(() => {
    refreshCities();
  });
});
