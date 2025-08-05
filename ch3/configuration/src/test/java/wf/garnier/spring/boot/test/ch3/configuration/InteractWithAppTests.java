package wf.garnier.spring.boot.test.ch3.configuration;

import java.util.List;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

// tag::interact-tests[]
@SpringBootTest
class InteractWithAppTests {

	@Autowired // <1>
	List<Thing> things;

	@Value("${demo.value}") // <2>
	int value;

	@Test
	void things() {
		var names = things.stream().map(Thing::name);
		assertThat(names).containsExactlyInAnyOrder("red", "pink", "green", "orange");
	}

	@Test
	void value() {
		assertThat(value).isEqualTo(1);
	}

	@Test
	void redThing(@Qualifier("redThing") @Autowired Thing redThing) { // <3>
		assertThat(redThing.name()).isEqualTo("red");
	}
	// tag::ignored[]

	@Test
	void missingBean(@Qualifier("transparentThing") @Autowired(required = false) Thing transparentThing) {
		assertThat(transparentThing).isNull();
	}

	@Test
	void supportsSpelExpression(@Value("#{'http://localhost:' + ${test.port}}") String someValue) {
		assertThat(someValue).isEqualTo("http://localhost:12345");
	}
	// end::ignored[]

}
// end::interact-tests[]
