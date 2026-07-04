package wf.garnier.spring.boot.test.ch3.configuration;

import org.springframework.stereotype.Component;

@Component
public record Gizmo(String name) {

	public Gizmo() {
		this("yellow");
	}
}
