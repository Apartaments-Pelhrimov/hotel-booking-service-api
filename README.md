# Hotel booking service Project

The **Hotel booking service** project serves as the back-end component of a hotel reservation system,
developed using Java Spring technologies. It provides an API for managing reservations, hotel rooms, clients, and other
aspects of accommodation booking.

## Technologies Used in the Project

- **Spring Boot:** for rapid creation and configuration of Spring applications, simplifying development and deployment.
- **Spring Web:** allows for creating RESTful APIs for data exchange with the system's clients.
- **Spring Data:** used for working with databases, including the use of Hibernate ORM for data access.
- **Spring Security:** ensures the security of the application, including user authentication and authorization.
- **Hibernate ORM:** used for database interaction and provides object-relational mapping (ORM) capabilities.
- **SQL:** used for creating and managing the database, including tables for hotels, rooms, reservations, and clients.
- **JUnit and Mockito:** used for writing and automating code testing to ensure its quality and reliability.
- **Maven:** used for managing project dependencies and building.

## Key Features

- Creation, updating, and deletion of hotels and apartments.
- Reservation of apartments by users.
- User authentication and authorization.
- Viewing reservation history and other hotel-related information.
- Testing using JUnit and Mockito to ensure the application's reliability and security.

This back-end Java Spring project allows for the management and utilization of information related to hotels and rooms,
making it convenient for users to book and enjoy their accommodations.

## Docker container configuration

To build executable `.jar` file
```shell
mvn package
```
To build and run Docker composed container with db:
```shell
mvn package
```
```shell
docker compose up
```
