package wf.garnier.spring.boot.test.ch1.widget;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AwesomeSpringBootTests {

	@Autowired
	private WidgetRepository repository;

	@Autowired
	private MockMvc mvc;

	@MockitoBean
	private WidgetValidator mockValidator;

	@Test
	void addWidget() throws Exception {
		doNothing().when(mockValidator).validateWidget(anyString());

		var location = mvc.perform(post("/widget").param("name", "test-widget"))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getHeader("location");

		var id = getWidgetId(location);
		var widget = repository.findById(id);
		assertThat(widget).isPresent();
		assertThat(widget.get().name()).isEqualTo("test-widget");
	}

	private static int getWidgetId(String location) {
		//@formatter:off
        var id = UriComponentsBuilder.fromUriString(location)
                .build()
                .getPathSegments()
                .getLast();
        //@formatter:on
		return Integer.parseInt(id);
	}

}
