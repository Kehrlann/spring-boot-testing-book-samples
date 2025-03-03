package wf.garnier.spring.boot.test.ch2.weather.weather.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class City {
    @Id
    @GeneratedValue
    private Long id;
    
    private String name;
    private double latitude;
    private double longitude;

    // JPA requires default constructor
    protected City() {}

    public City(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}