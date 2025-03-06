package wf.garnier.spring.boot.test.ch2.weather.weather.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "preferred_city")
public class PreferredCity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    // JPA requires default constructor
    protected PreferredCity() {}

    public PreferredCity(City city) {
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public City getCity() {
        return city;
    }
}