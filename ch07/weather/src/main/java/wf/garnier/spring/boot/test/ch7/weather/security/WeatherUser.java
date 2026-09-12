package wf.garnier.spring.boot.test.ch7.weather.security;

/**
 * A user of the application, as seen by other modules. Notably, this exposes neither
 * credentials nor Spring Security types: the {@code UserDetails} implementation stays
 * inside this module.
 */
public interface WeatherUser {

	UserId getId();

	String getUsername();

	Email getEmail();

}
