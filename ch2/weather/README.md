# Chapter 2: Testing a Hellow World application

## The application

The application under test is a "weather" app, showing weather conditions in different cities.

Run it with:

```shell
./mvnw spring-boot:run
```

Access it via http://localhost:8080/.

The application uses the https://open-meteo.com API for getting weather data.
If you cannot access the API, you can run the app with the `local` profile and get random data instead:

```shell
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

## Test classes

- **`WeatherApplicationTests`** - Default test class created by Spring Initializr.
- **`ExampleTests`** - Example tests using some of the features of Spring-Boot tests.
