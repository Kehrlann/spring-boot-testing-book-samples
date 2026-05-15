package wf.garnier.spring.boot.test.ch5.weather.weather.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wf.garnier.spring.boot.test.ch5.weather.weather.WeatherData;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import static wf.garnier.spring.boot.test.ch5.weather.weather.internal.CacheConfiguration.WEATHER_CACHE_NAME;

@Profile("!local")
// tag::class[]
@Service
class OpenMeteoWeatherDataService implements WeatherDataService {

	// end::class[]
	private static final Logger log = LoggerFactory.getLogger(OpenMeteoWeatherDataService.class);

	// tag::fields[]
	private final RestClient restClient;

	static final String API_URL = "https://api.open-meteo.com/v1/forecast";

	// end::fields[]
	// tag::constructor[]
	OpenMeteoWeatherDataService(RestClient.Builder restClientBuilder) {
		this.restClient = restClientBuilder.baseUrl(API_URL).build();
	}
	// end::constructor[]

	@Override
	@Cacheable(WEATHER_CACHE_NAME)
	public WeatherData getCurrentWeather(double latitude, double longitude) {
		try {
			ApiResponse response = restClient.get()
				.uri(uriBuilder -> uriBuilder.queryParam("latitude", latitude)
					.queryParam("longitude", longitude)
					.queryParam("current", "temperature_2m,windspeed_10m,weathercode")
					.build())
				.retrieve()
				.body(ApiResponse.class);
			return new WeatherData(response.current.temperature, response.current.windspeed,
					response.current.weathercode);
		}
		catch (HttpClientErrorException.BadRequest e) {
			// From Open Meteo docs:
			// In case an error occurs, for example a URL parameter is not correctly
			// specified, a JSON error object is returned with a HTTP 400 status code.
			throw new IllegalStateException("Could not fetch data for lat=%s, lon=%s".formatted(latitude, longitude),
					e);
		}
		catch (HttpServerErrorException e) {
			log.error("Got error fetching weather data", e);
			return new WeatherData();
		}
	}

	private record ApiResponse(Current current) {
	}

	private record Current(@JsonProperty("temperature_2m") double temperature,
			@JsonProperty("windspeed_10m") double windspeed, int weathercode) {
	}

	// tag::class[]

}
// end::class[]