var isCompactMode = false;
var currentCities = null;

function getDisplayModeFromURL() {
  var urlParams = new URLSearchParams(window.location.search);
  var display = urlParams.get("display");
  return display === "compact";
}

function updateURLWithDisplayMode(mode) {
  var url = new URL(window.location);
  url.searchParams.set("display", mode);
  window.history.replaceState({}, "", url);
}

function loadWeather() {
  return fetch("/api/weather").then((response) => response.json());
}

function renderCity(cityWeather, isCompact) {
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

function refreshCities() {
  return loadWeather().then((cities) => {
    currentCities = cities;
    var citiesGrid = document.querySelector(".cities-grid");
    citiesGrid.innerHTML = currentCities
      .map((city) => renderCity(city, isCompactMode))
      .join("\n");
  });
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
  return fetch("/api/city", {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ id: cityId }),
  }).then((response) => response.ok);
}

function searchCities(query) {
  return fetch(`/api/city?q=${encodeURIComponent(query)}`).then((response) =>
    response.json(),
  );
}

function AutocompleteDropdown(
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

AutocompleteDropdown.prototype.setupEventListeners = function () {
  this.input.addEventListener("input", this.handleInput.bind(this));
  this.input.addEventListener("keydown", this.handleKeydown.bind(this));
  this.input.addEventListener("blur", this.handleBlur.bind(this));
  this.results.addEventListener("click", this.handleClick.bind(this));
  this.results.addEventListener(
    "mouseenter",
    this.handleMouseEnter.bind(this),
    true,
  );
};

AutocompleteDropdown.prototype.handleInput = function (e) {
  const query = e.target.value.trim();
  if (query.length >= 2) {
    this.searchFunction(query).then((cities) => {
      this.results.innerHTML = cities
        .map(
          (city) =>
            `<div class="autocomplete-item" data-id="${city.id}">
                      ${city.name} (${city.country})
                  </div>`,
        )
        .join("");
      this.selectedIndex = -1;
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

document.addEventListener("DOMContentLoaded", function () {
  var citySearch = document.getElementById("citySearch");
  var cityResults = document.getElementById("cityResults");

  // Initialize display mode from URL
  isCompactMode = getDisplayModeFromURL();
  setDisplayMode(isCompactMode);

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
    },
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

  // Initialize display toggle buttons
  var fullButton = document.getElementById("button-display-full");
  var compactButton = document.getElementById("button-display-compact");

  // Set initial button states based on URL
  if (isCompactMode) {
    compactButton.classList.add("button-primary");
    fullButton.classList.remove("button-primary");
  } else {
    fullButton.classList.add("button-primary");
    compactButton.classList.remove("button-primary");
  }

  fullButton.addEventListener("click", function () {
    fullButton.classList.add("button-primary");
    compactButton.classList.remove("button-primary");
    updateURLWithDisplayMode("full");
    setDisplayMode(false);
  });

  compactButton.addEventListener("click", function () {
    compactButton.classList.add("button-primary");
    fullButton.classList.remove("button-primary");
    updateURLWithDisplayMode("compact");
    setDisplayMode(true);
  });
});
