async function loadWeather() {
    const response = await fetch('/api/weather');
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
    const citiesGrid = document.querySelector('.cities-grid');
    citiesGrid.innerHTML = cities.map(renderCity).join('\n');
}

async function addCity(cityId) {
    const response = await fetch('/api/city', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ id: cityId })
    });
    return response.ok;
}

async function removeCity(cityId) {
    const response = await fetch('/api/city', {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ id: cityId })
    });
    return response.ok;
}

async function searchCities(query) {
    const response = await fetch(`/api/city?q=${encodeURIComponent(query)}`);
    return await response.json();
}

let selectedCityId = null;

document.addEventListener('DOMContentLoaded', () => {
    const addCityForm = document.querySelector('form#add-city');
    const citySearch = document.getElementById('citySearch');
    const cityResults = document.getElementById('cityResults');
    
    addCityForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        if (selectedCityId) {
            const success = await addCity(selectedCityId);
            if (success) {
                await refreshCities();
                citySearch.value = '';
                cityResults.innerHTML = '';
                selectedCityId = null;
            }
        }
    });
    
    citySearch.addEventListener('input', async (e) => {
        const query = e.target.value.trim();
        if (query.length >= 2) {
            const cities = await searchCities(query);
            cityResults.innerHTML = cities.map(city => 
                `<div class="autocomplete-item" data-id="${city.id}">
                    ${city.name} (${city.country})
                </div>`
            ).join('');
        } else {
            cityResults.innerHTML = '';
        }
        selectedCityId = null;
    });
    
    cityResults.addEventListener('click', (e) => {
        if (e.target.classList.contains('autocomplete-item')) {
            selectedCityId = parseInt(e.target.dataset.id);
            citySearch.value = e.target.textContent;
            cityResults.innerHTML = '';
        }
    });
    
    const citiesGrid = document.querySelector('.cities-grid');
    citiesGrid.addEventListener('submit', async (e) => {
        if (e.target.matches('form[data-role="delete-city"]')) {
            e.preventDefault();
            const formData = new FormData(e.target);
            const cityId = parseInt(formData.get('cityId'));
            if (cityId) {
                const success = await removeCity(cityId);
                if (success) {
                    await refreshCities();
                }
            }
        }
    });
});