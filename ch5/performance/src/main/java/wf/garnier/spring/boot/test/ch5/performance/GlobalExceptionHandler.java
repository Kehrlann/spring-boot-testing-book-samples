package wf.garnier.spring.boot.test.ch5.performance;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
		return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(Map.of("error", "An unexpected error occurred"));
	}

}
