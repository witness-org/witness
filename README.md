# witness

This project aims to provide an application to be used by weightlifters to log their workouts, monitor their progress
and create training programs. It consists of a server which is realized as a
[Spring Boot](https://spring.io/projects/spring-boot) REST API and a client written in [Flutter](https://flutter.dev/).

Although Flutter supports multiple target architectures such as Android, iOS, web and native desktop environments, the
client is, at present, optimized for Android only.

## Features

The ultimate goal is an application for both Android and iOS as well as a Web application that can be used daily by
anyone interested in weightlifting as regular exercise. Below is a description of the system's core functionalities.

Features planned for the future may be found in the issue tracker.

### User Management

Users may register themselves or sign in via an email/password combination. The system supports differentiating between
different privilege levels (regular, premium and admin users).

Security features related to the user management (e.g. secure storage of credentials, JWT generation) are delegated to
a [Firebase](https://firebase.google.com/) authentication server. The Spring application keeps its user data in sync
with the database stored on the remote Firebase server.

Please consult the Wiki for more information about the architecture of the server application.

### Exercise Management

Exercises are the main component for creating training programs and logging workouts. They are defined by the following
characteristics:

* name
* description
* (multiple) muscle groups
* (multiple) logging types (e.g. time or reps)

Furthermore, there is the notion of _initial exercises_ and _user exercises_. Initial exercises are included with the
application, available to all users and may neither be edited nor deleted by regular users (only by admins). User
exercises, on the other hand, are created by a specific user, only available to them and may be edited as well as
deleted by them.

#### Exercise History

Exercises that have been logged by means of a previous workout appear in an aggregated history view. It shows all the
log entries of each exercise with information about logged reps, time, RPE, resistance bands comments (if available).

#### Exercise Statistics

Another separate view displays exercise statistics, i.e. shows and visualizes how exercise-log-related metrics have
changed over the course of time.

### Workout Logs

A core feature of the app is logging workouts. Workouts are visualized on a per-day basis and consist of one or more
exercise logs. Each exercise log, in turn, comprises (possible multiple) set logs.

Users may log workouts from scratch, i.e. choose exercises to add to the workout bit by bit. It is also possible to copy
workouts from a previous day (so that only certain parameters like number of reps or RPE need to be adjusted). Lastly,
it is also possible to create workouts from a workout specification defined by a training program.

### Training Programs

TBD

## Building and Running the Applications

For information on how to set up the development environment for server and client and how to build as well as execute
them, refer to the guides in their respective directory ([server](server/README.md), [client](client/README.md)).

## Continuous Integration

We have set up a CI pipeline consisting of the four jobs in two stages:

1. stage `check`: The jobs `check:client` and `check:server` verify that both projects adhere to our coding style by
means of a static analysis.
2. stage `test`: The jobs `test:client` and `test:server` run unit as well as integration tests on both projects.
