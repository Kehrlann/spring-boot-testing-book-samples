package wf.garnier.spring.boot.test.ch5.performance;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class Controller745Test {

	@WebMvcTest(Controller745.class)
	@Import(SecurityConfig.class)
	@Tag("single")
	@Nested
	class SingleControllerTest {

		@Autowired
		MockMvc mockMvc;

		@Test
		void listReturnsOk() throws Exception {
			mockMvc.perform(get("/api/resource745"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.controller").value("Controller745"));
		}

		@Test
		void getByIdReturnsOk() throws Exception {
			mockMvc.perform(get("/api/resource745/42"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("42"));
		}

	}

	@WebMvcTest
	@Import(SecurityConfig.class)
	@Tag("all")
	@Nested
	class AllControllerTest {

		@Autowired
		MockMvc mockMvc;

		@Test
		void listReturnsOk() throws Exception {
			mockMvc.perform(get("/api/resource745"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.controller").value("Controller745"));
		}

		@Test
		void getByIdReturnsOk() throws Exception {
			mockMvc.perform(get("/api/resource745/42"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("42"));
		}

	}

}
