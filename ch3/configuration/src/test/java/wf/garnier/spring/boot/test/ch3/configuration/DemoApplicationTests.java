package wf.garnier.spring.boot.test.ch3.configuration;

import java.util.List;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DemoApplicationTests {

	@Test
	void things(@Autowired List<Thing> things) {
		var names = things.stream().map(Thing::name);
		assertThat(names).containsExactlyInAnyOrder("bean-one", "bean-two", "configuration-test-package");
	}

	@Test
	void properties(@Autowired DemoProperties demoProperties) {
		assertThat(demoProperties.message()).isEqualTo("Hello, world!");
		assertThat(demoProperties.value()).isEqualTo(42);
	}

}
