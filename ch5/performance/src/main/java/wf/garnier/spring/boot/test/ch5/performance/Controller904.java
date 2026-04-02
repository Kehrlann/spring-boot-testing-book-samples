package wf.garnier.spring.boot.test.ch5.performance;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resource904")
public class Controller904 {

	@GetMapping
	public Map<String, String> list() {
		return Map.of("controller", "Controller904", "action", "list");
	}

	@GetMapping("/{id}")
	public Map<String, String> getById(@PathVariable String id) {
		return Map.of("controller", "Controller904", "action", "get", "id", id);
	}

}
