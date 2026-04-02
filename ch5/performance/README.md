# Testing Performance Experiment

This experiment demonstrates the performance impact of different test context strategies. 

*Note: This is an extremely unscientific experiment, just to give you an idea of the orders of
magnitude involved.*

## Running Tests

You can run the tests using the Maven wrapper by specifying the test group:

```bash
./mvnw test -Dgroups=all
# or
./mvnw test -Dgroups=single
```

## Results

| Strategy | Command | Execution Time | Description |
|---|---|---|---|
| **Single Context** | `./mvnw test -Dgroups=all` | ~15 seconds | Runs all tests using a single, shared test context. |
| **Sliced Context** | `./mvnw test -Dgroups=single` | 5 - 7 minutes | Runs tests with a separate context slice per controller. |
