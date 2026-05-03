package wf.garnier.spring.boot.test.ch5.weather.city;

public class CityNotFoundException extends RuntimeException {
	public CityNotFoundException(long id) {
		super("City with id " + id + " not found");
	}
}
