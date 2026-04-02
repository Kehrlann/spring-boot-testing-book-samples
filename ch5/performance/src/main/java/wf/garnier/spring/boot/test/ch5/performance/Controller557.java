package wf.garnier.spring.boot.test.ch5.performance;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resource557")
public class Controller557 {

	@GetMapping
	public Map<String, String> list() {
		return Map.of("controller", "Controller557", "action", "list");
	}

	@GetMapping("/{id}")
	public Map<String, String> getById(@PathVariable String id) {
		return Map.of("controller", "Controller557", "action", "get", "id", id);
	}

}
