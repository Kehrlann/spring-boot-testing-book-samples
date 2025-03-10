package wf.garnier.spring.boot.test.ch2.weather.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.Objects;

@Entity
public class City {

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	private String country;

	private double latitude;

	private double longitude;

	// JPA requires default constructor
	protected City() {
	}

	public City(String name, String country, double latitude, double longitude) {
		this.name = name;
		this.country = country;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;

		City city = (City) o;
		return Objects.equals(id, city.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "City{" +
			   "id=" + id +
			   ", name='" + name + '\'' +
			   ", country='" + country + '\'' +
			   ", latitude=" + latitude +
			   ", longitude=" + longitude +
			   '}';
	}
}