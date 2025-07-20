package wf.garnier.spring.boot.test.ch3.configuration;

import java.util.List;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TestConfigurationTests {

	@Test
	void thing(@Autowired List<Thing> things) {
		assertThat(things).map(Thing::name)
			.containsExactlyInAnyOrder("bean-one", "bean-two", "testconfig", "configuration-test-package");
	}

	@TestConfiguration
	static class TestConfig {

		@Bean
		Thing testConfigThing() {
			return new Thing("testconfig");
		}

	}

}
