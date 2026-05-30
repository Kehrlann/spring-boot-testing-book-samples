package wf.garnier.spring.boot.test.ch5.weather.city.internal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import wf.garnier.spring.boot.test.ch5.weather.city.City;

import java.util.Objects;

@Entity
@Table(name = "city")
public class CityEntity implements City {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String name;

	private String country;

	private double latitude;

	private double longitude;

	// JPA requires default constructor
	protected CityEntity() {
	}

	public CityEntity(String name, String country, double latitude, double longitude) {
		this.name = name;
		this.country = country;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public double getLatitude() {
		return latitude;
	}

	@Override
	public double getLongitude() {
		return longitude;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;

		CityEntity city = (CityEntity) o;
		return Objects.equals(id, city.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "City{" + "id=" + id + ", name='" + name + '\'' + ", country='" + country + '\'' + ", latitude="
				+ latitude + ", longitude=" + longitude + '}';
	}

}