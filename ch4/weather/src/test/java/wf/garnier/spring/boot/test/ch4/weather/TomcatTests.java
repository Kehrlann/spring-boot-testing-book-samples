package wf.garnier.spring.boot.test.ch4.weather;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch4.weather.infrastructure.TomcatAccessRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.test.web.servlet.client.assertj.RestTestClientResponse;
import org.springframework.web.client.RestClient;
import static org.assertj.core.api.Assertions.assertThat;

class TomcatTests {

	@Nested
	@SpringBootTest
	@AutoConfigureMockMvc
	class MockMvcTests {

		@Autowired
		MockMvcTester mvc;

		@Autowired
		TomcatAccessRepository repo;

		@Test
		void doesNotRecordAccess() {
			mvc.get().uri("/api/weather").exchange().assertThat().hasStatus(HttpStatus.OK);

			assertThat(repo.getAccessRecords()).isEmpty();
		}

		@Test
		void doesNotDisplayCustom404Page() {
			var response = mvc.get().uri("/does-not-exist").exchange();

			assertThat(response).hasStatus(HttpStatus.NOT_FOUND)
				.hasErrorMessage("No static resource does-not-exist.")
				.bodyText()
				.isEmpty();
		}

	}

	@Nested
	@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
	@AutoConfigureRestTestClient
	class FullServerTests {

		@LocalServerPort
		int port;

		@Autowired
		TomcatAccessRepository repo;

		@Autowired
		RestTestClient testClient;

		RestClient restClient;

		@BeforeEach
		void setUp() {
			restClient = RestClient.builder().baseUrl("http://localhost:%s".formatted(port)).build();
		}

		@Test
		void displaysCustom404Page() {
			var responseSpec = testClient.get().uri("/does-not-exist").accept(MediaType.TEXT_HTML).exchange();

			var response = RestTestClientResponse.from(responseSpec);
			assertThat(response).hasStatus(HttpStatus.NOT_FOUND)
				.bodyText()
				.containsIgnoringCase("404 NOT FOUND")
				.containsIgnoringCase("This is not the page you are looking for");
		}

		@Test
		void recordsAccess() {
			var response = restClient.get().uri("/api/weather".formatted(port)).retrieve();

			assertThat(response.toBodilessEntity().getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(repo.getAccessRecords()).hasSize(1)
				.first()
				.extracting(TomcatAccessRepository.Access::method, TomcatAccessRepository.Access::path)
				.containsExactly("GET", "/api/weather");
		}

		/**
		 * Our {@link TomcatAccessRepository} stores data in a ring buffer. This test
		 * performs a sanity check on the ring buffer behavior.
		 * <p>
		 * If this was production code the {@code RingBuffer} class should be a separate
		 * class, with dedicated tests... or even better, an off-the-shelf implementation
		 * ;)
		 */
		@Test
		void ringBuffer() {
			for (int i = 0; i < 50; i++) {
				var response = RestClient.create()
					.get()
					.uri("http://localhost:%s/api/weather".formatted(port))
					.retrieve();
				assertThat(response.toBodilessEntity().getStatusCode()).isEqualTo(HttpStatus.OK);
			}
			for (int i = 0; i < 49; i++) {
				var response = RestClient.create()
					.get()
					.uri("http://localhost:%s/api/city?q=Paris".formatted(port))
					.retrieve();
				assertThat(response.toBodilessEntity().getStatusCode()).isEqualTo(HttpStatus.OK);
			}

			assertThat(repo.getAccessRecords()).hasSize(50);
			assertThat(repo.getAccessRecords()).first()
				.extracting(TomcatAccessRepository.Access::path)
				.isEqualTo("/api/weather");
			assertThat(repo.getAccessRecords()).last()
				.extracting(TomcatAccessRepository.Access::path)
				.isEqualTo("/api/city");
		}

	}

}
