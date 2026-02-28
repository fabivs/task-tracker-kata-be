# Kotlin Task Management System Kata (Backend)

# Assignment

The goal of this assignment is to deliver a production-grade ready and functional application.
The application will consist of two separate applications, a backend and a frontend.

## Core Technology Stack Guidelines

You should use the following technologies:

- **Backend**: Kotlin
- **Frontend**: Next.js or React
- **API Protocol**: HTTP(REST)
- **Docker**: A `docker-compose.yml` file for quick setup

_Note on Persistence_: Data persistence for tasks can be managed using an in-memory
data structure or a database depending on your choice and effort required.

The backend and frontend will be two different projects in separate repositories,this is the **Backend application**.

## Functional requirements

You are required to build a Personal Task Management Application that allows a user to
manage a simple to-do list.

**Task Management**

- **Creation**: The user must be able to create a new task.
- **Viewing**: The application must display all tasks.
- **Update**: The user needs a way to update the task's detail and status.
- **Filtering**: The user should be able to filter the displayed task list.

## Bonus features (nice-to-haves)

- **Authentication**: Implement a simple authentication mechanism to associate tasks
with a single user.
- **CI/CD Workflow**
- **API Routes in NextJS**

You can also incorporate any additional features you deem appropriate.

# Solution

The project has been implemented in Kotlin with Ktor (a simple and minimalistic web framework).
Dependency injection and Use case patterns are used to decouple the business logic from the external Routing.

- REST endpoints are defined in the `Routing.kt` file.
- Use cases are defined in the `usecase` package and contain the core business logic of the application.
- The storage repository is a simple in memory representation with a `ConcurrentHashMap` with the tasks information.
- A `DependencyContainer` class has been defined to instantiate the components, but, given the simple scope of the 
  project, there is currently no form of dependency injection that was required to be implemented.

Tests:
- `RoutingTest` contains the Integration tests over the endpoints of the application
- the `domain` and `usecase` packages contain Unit tests
- the `repository` package contains the repository tests, which normally would be Integration tests, but in this case,
  because the repository is a simple in-memory representation, they are basically Unit tests as well.

## Differences between actual production code

- tests for the usecases would have to mock the Repository, in order to be Unit tests, in this case was not necessary
  for the reasons stated above.


# Run the project

Requirements:

- JDK and Gradle
- Docker (if opting for running the application with Docker)
- Make (not strictly required, you can run the gradle and docker commands inside the Makefile manually)

Note: use `$ make help` to see all available make commands.

Alternatively, you can also use the IntelliJ Idea IDE to perform all of these tasks.

## Without Docker

Start the project:

```
make build
make run
```

The APIs will be available locally at: <http://127.0.0.1:8080>.

Example usages: -- TODO: add example APIs

- `GET http://127.0.0.1:8080/xxx/yyy`

Run the tests:

```
make test
```

## Within a Docker container

A `Dockerfile` has been provided to build a docker image for the project.

Build the docker image for the project:

```
make build-docker
```

Run the image (on port 8080):

```
make run-docker
