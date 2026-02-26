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
