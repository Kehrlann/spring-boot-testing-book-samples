package wf.garnier.spring.boot.test.ch1.widget;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
class WidgetController {

	private final WidgetService widgetService;

	public WidgetController(WidgetService widgetService) {
		this.widgetService = widgetService;
	}

	@GetMapping("/widget")
	public List<Widget> getWidget() {
		return widgetService.listWidgets();
	}

	@GetMapping("/widget/{id}")
	public ResponseEntity<Widget> getWidget(@PathVariable int id) {
		return widgetService.findWidget(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping("/widget")
	public ResponseEntity<?> createWidget(@RequestParam String name) {
		try {
			var newWidget = widgetService.createWidget(name);
			var createdUri = ServletUriComponentsBuilder.fromCurrentRequest()
				.replacePath("/widget/{id}")
				.buildAndExpand(newWidget.id())
				.toUri();
			return ResponseEntity.created(createdUri).build();
		}
		catch (InvalidWidgetException e) {
			return ResponseEntity.badRequest().body("Widget creation was rejected: " + e.getMessage());
		}
	}

}
