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

document.addEventListener('DOMContentLoaded', () => {
    const addCityForm = document.querySelector('form#add-city');
    addCityForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData(addCityForm);
        const cityId = parseInt(formData.get('cityId'));
        if (cityId) {
            const success = await addCity(cityId);
            if (success) {
                await refreshCities();
                addCityForm.reset();
            }
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