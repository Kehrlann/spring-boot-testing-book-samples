package wf.garnier.spring.boot.test.ch6.weather.weather.internal;

import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch6.weather.weather.WeatherData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withTooManyRequests;

@RestClientTest(components = { OpenMeteoWeatherDataService.class })
class OpenMeteoWeatherDataServiceTest {

	@Autowired
	private OpenMeteoWeatherDataService service;

	@Autowired
	private MockRestServiceServer mockServer;

	// ... tests ...
	@Test
	void getWeatherData() {
		mockServer.expect(queryParam("latitude", "32.99"))
			.andExpect(queryParam("longitude", "-97.68"))
			.andRespond(withSuccess("""
					{
						"current": {
							"temperature_2m": 3.1,
							"windspeed_10m": 52.0,
							"weathercode": 95
						}
					}
					""", MediaType.APPLICATION_JSON));

		var response = service.getCurrentWeather(32.99, -97.68);

		assertThat(response.temperature()).isEqualTo(3.1);
		assertThat(response.windspeed()).isEqualTo(52.0);
		assertThat(response.weather()).isEqualTo("Thunderstorm");
	}

	@Test
	void http500Recover() {
		mockServer.expect(anything()).andRespond(withServerError());

		var response = service.getCurrentWeather(32.99, -97.68);

		assertThat(response).isEqualTo(new WeatherData());
	}

	@Test
	void http400Throw() {
		//@formatter:off
		mockServer.expect(anything())
				.andRespond(withBadRequest());

		assertThatThrownBy(() -> service.getCurrentWeather(32.99, -97.68))
				.isInstanceOf(IllegalArgumentException.class);
		//@formatter:on
	}

	@Test
	void customSetup() {
		mockServer.expect(request -> {
			//@formatter:off
			var queryParams = UriComponentsBuilder
					.fromUri(request.getURI())
					.build()
					.getQueryParams();
			//@formatter:on
			var hasLatitude = queryParams.containsKey("latitude");
			var hasLongitude = queryParams.containsKey("longitude");
			if (!hasLatitude || !hasLongitude) {
				throw new AssertionError("missing lat/lon");
			}
		}).andRespond(
		//@formatter:off
			withStatus(HttpStatus.I_AM_A_TEAPOT)
					.header("X-Custom-Error", "Teapot!")
					.body("There was no coffee left")
			//@formatter:on
		);

		assertThatThrownBy(() -> service.getCurrentWeather(32.99, -97.68)).isInstanceOf(HttpClientErrorException.class)
			.extracting("statusCode", "statusText")
			.containsExactly(HttpStatus.I_AM_A_TEAPOT, "I'm a teapot");
	}

	@Test
	void verifications() {
		mockServer.expect(ExpectedCount.between(1, 2), queryParam("latitude", "32.99")).andRespond(withSuccess("""
				{
					"current": {
						"temperature_2m": 0,
						"windspeed_10m": 0.0,
						"weathercode": 0
					}
				}""", MediaType.APPLICATION_JSON));

		assertThatThrownBy(mockServer::verify).isInstanceOf(AssertionError.class);

		service.getCurrentWeather(32.99, -97.68);
		mockServer.verify();

		service.getCurrentWeather(32.99, -97.68);
		assertThatThrownBy(() -> service.getCurrentWeather(32.99, -97.68)).isInstanceOf(AssertionError.class);
	}

	/**
	 * Manually bind a {@link RestClient} to a {@link MockRestServiceServer}, without
	 * needing the Spring Boot test slice.
	 */
	@Test
	void manuallyBind() {
		var clientBuilder = RestClient.builder();
		var mockServer = MockRestServiceServer.bindTo(clientBuilder)
			// by default, the incoming request MUST match the expected
			// requests in order. You can turn off this behavior, but unless
			// you are dealing with parallel requests, I do not recommend this.
			.ignoreExpectOrder(true)
			.build();
		var client = clientBuilder.build();

		mockServer.expect(requestTo(containsString("/spanish"))).andRespond(withSuccess("hola", MediaType.TEXT_PLAIN));
		mockServer.expect(ExpectedCount.between(1, 3), requestTo(containsString("/english")))
			.andRespond(withSuccess("hello", MediaType.TEXT_PLAIN));
		mockServer.expect(ExpectedCount.once(), requestTo(containsString("/french"))).andRespond(withTooManyRequests());

		var firstRequest = client.get().uri("https://example.com/english").retrieve().toEntity(String.class);
		assertThat(firstRequest.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(firstRequest.getBody()).isEqualTo("hello");
		var secondRequest = client.get().uri("https://example.com/spanish").retrieve().toEntity(String.class);
		assertThat(secondRequest.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(secondRequest.getBody()).isEqualTo("hola");

		// Second time works
		client.get().uri("https://example.com/english").retrieve().toBodilessEntity();
		// Third time works
		client.get().uri("https://example.com/english").retrieve().toBodilessEntity();
		// Fourth request is not configured so it fails
		assertThatThrownBy(() -> client.get().uri("https://example.com/english").retrieve().toBodilessEntity())
			.isInstanceOf(AssertionError.class);
	}

	@Test
	void verifyExpectations() {
		var clientBuilder = RestClient.builder();
		var mockServer = MockRestServiceServer.createServer(clientBuilder);
		var client = clientBuilder.build();

		mockServer.expect(ExpectedCount.between(1, 3), requestTo(containsString("/english"))).andRespond(withSuccess());
		mockServer.expect(ExpectedCount.once(), requestTo(containsString("/french"))).andRespond(withSuccess());

		// 1 request to /english: ok
		client.get().uri("https://example.com/english").retrieve().toEntity(String.class);
		assertThatThrownBy(mockServer::verify).isInstanceOf(AssertionError.class);
		// 2 requests to /english: still ok
		// 0 request to /french: failing
		client.get().uri("https://example.com/english").retrieve().toEntity(String.class);
		// 2 requests to /english: still ok
		// 1 request to /french: ok
		client.get().uri("https://example.com/french").retrieve().toEntity(String.class);
		mockServer.verify();
	}

}
