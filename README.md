# [WIP] Pedal Price

This project will (eventually) allow users to create alerts for when a product they are interested
in goes on offer.
It will source the product prices from multiple websites by scraping them, starting with
[Sigma Sports](https://sigmasports.com)

## Requirements

- docker
- [tilt](https://docs.tilt.dev/install.html) - _optional_
- JDK 17

## Running

First build the application jar:

```shell
./gradlew clean build
```

### Using tilt:

```shell
tilt up
```

### Using docker-compose

```shell
docker-compose up -d
```

## Testing

### Unit tests

```shell
./gradlew clean test
```

### Integration tests (without unit tests)

```shell
./gradlew clean check -x test
```

### All tests

```shell
./gradlew clean check
```



