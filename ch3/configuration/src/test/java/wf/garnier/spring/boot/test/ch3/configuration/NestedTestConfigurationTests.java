package wf.garnier.spring.boot.test.ch3.configuration;

import java.util.List;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class NestedTestConfigurationTests {

	@Test
	void things(@Autowired List<Thing> things) {
		assertThat(things).map(Thing::name)
			.containsExactlyInAnyOrder("bean-one", "bean-two", "nested-testconfig-bean", "configuration-test-package");
	}

	@Test
	void properties(@Autowired DemoProperties demoProperties) {
		assertThat(demoProperties.message()).isEqualTo("Hello, world!");
		assertThat(demoProperties.value()).isEqualTo(1);
	}

	@TestConfiguration
	static class TestConfig {

		@Bean
		Thing testConfigThing() {
			return new Thing("nested-testconfig-bean");
		}

	}

}
