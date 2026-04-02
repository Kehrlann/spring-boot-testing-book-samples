package wf.garnier.spring.boot.test.ch5.performance;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resource187")
public class Controller187 {

	@GetMapping
	public Map<String, String> list() {
		return Map.of("controller", "Controller187", "action", "list");
	}

	@GetMapping("/{id}")
	public Map<String, String> getById(@PathVariable String id) {
		return Map.of("controller", "Controller187", "action", "get", "id", id);
	}

}
