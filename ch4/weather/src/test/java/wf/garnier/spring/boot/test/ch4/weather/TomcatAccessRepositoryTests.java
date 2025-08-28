package wf.garnier.spring.boot.test.ch4.weather;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch4.weather.infrastructure.TomcatAccessRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.web.client.RestClient;
import static org.assertj.core.api.Assertions.assertThat;

class TomcatAccessRepositoryTests {

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

	}

	@Nested
	@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
	class FullServerTests {

		@LocalServerPort
		int port;

		@Autowired
		TomcatAccessRepository repo;

		@Test
		void recordsAccess() {
			var response = RestClient.create().get().uri("http://localhost:%s/api/weather".formatted(port)).retrieve();

			assertThat(response.toBodilessEntity().getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(repo.getAccessRecords()).hasSize(1)
				.first()
				.extracting(TomcatAccessRepository.Access::method, TomcatAccessRepository.Access::path)
				.containsExactly("GET", "/api/weather");
		}

	}

}
