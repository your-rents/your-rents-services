# YourRents Services

Modules for the YourRents application.

[![Build](https://github.com/your-rents/your-rents-services/actions/workflows/maven.yml/badge.svg)](https://github.com/your-rents/your-rents-services/actions/workflows/maven.yml)

## Prerequisites

- Java 17+
- A Docker environment supported by Testcontainers: <https://www.testcontainers.org/supported_docker_environment/> (a recent local Docker installation is enough)

## Getting Started

Clone the repository and run the following command to build the project:

```shell
./mvnw clean verify
```

## YourRents Services Geodata

This module contains the geodata service.

### Running the service

Move to the `your-rents-services-geodata` directory:

```shell
cd your-rents-services-geodata
```

and run the following command:

```shell
./mvnw spring-boot:run
```

Try the services at <http://localhost:8080/swagger-ui.html>.

You can inspect the database using Adminer at <http://localhost:28080> with the following connection parameters:

- **System:** PostgreSQL
- **Server:** postgres-yrs-geodata
- **Username:** yrs_geodata
- **Password:** yrs_geodata
- **Database:** yrs_geodata

### Cleaning the database

If you need a fresh database:

- stop the service
- remove the Docker containers:

     ```shell
     docker compose down
     ```

- Remove the `your-rents-services-geodata_postgres-yrs-geodata` volume:

     ```shell
     docker volume rm your-rents-services-geodata_postgres-yrs-geodata
     ```

- Then restart the service