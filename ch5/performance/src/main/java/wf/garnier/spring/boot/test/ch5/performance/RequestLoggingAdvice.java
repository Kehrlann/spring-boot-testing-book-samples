package wf.garnier.spring.boot.test.ch5.performance;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class RequestLoggingAdvice {

	@ModelAttribute("requestTimestamp")
	public long addRequestTimestamp(HttpServletRequest request) {
		return System.currentTimeMillis();
	}

}
