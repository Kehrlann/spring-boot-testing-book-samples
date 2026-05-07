package wf.garnier.spring.boot.test.ch5.weather.preferences;

import org.springframework.data.jpa.repository.JpaRepository;

interface PreferencesRepository extends JpaRepository<Preferences, Long> {

}
