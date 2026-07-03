# Reading Assignment Portal

A lightweight teacher/student reading assignment portal built with Java Spring Boot.

## Overview

This application allows teachers to assign books to students and track reading progress.

Teachers can:

- View available books
- Create reading assignments for students
- Set due dates
- View assignment status and minutes read
- Track minutes read by students

Students can:

- View assigned readings
- Open assigned book links
- Update assignment status

## Running the Project
To demo the project, visit the URL - https://reading-assignment-ui.vercel.app/
This brings you to a page to login as a teacher or student.
You can login with the demo user credentials provided below.
### Demo Teacher
Role: Teacher, Email: teacher@example.com, password: Demo#Teacher
### Demo Students
Role: Student, Email: Student@example.com, password: Demo#Student
Role: Student, Email: alex@example.com, password: Alex#Reader
Role: Student, Email: sam@example.com, password: Sam#Learner

- First login as a teacher and then create an assignment for one or more students.
- Then login as a student on a different browser and view the assigned assignment.
- Update the assignment status while logged in as the student.
- View the assignment status and minutes read on the teacher dashboard. This will update automatically update every 60 seconds. You can also refresh the page to see minutes read updates immediately.

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- H2 Database
- Maven
- Angular

## Features Implemented

### Books

- Seeded list of books available for assignment
- API endpoint to retrieve books

### Users

- Seeded demo users
- Teacher and student roles
- API endpoint to retrieve students

### Assignments

- Teachers can create assignments for one or more students
- Assignments include:
    - Book
    - Teacher
    - Student
    - Due date
    - Read Start time
    - Read finish time
    - Status
    - Minutes read
- Students can update assignment progress
- Teachers can view assignment progress

## Assignment Statuses

Assignments support the following statuses:

NOT_STARTED, IN_PROGRESS, and COMPLETED

## H2 Database Console

The local H2 database console is available at:
[https://reading-assignment.onrender.com/h2-console](https://reading-assignment.onrender.com/h2-console)


Use the JDBC URL configured in `src/main/resources/application.properties`.

Typical local values:
JDBC URL: jdbc:h2:mem:reading_assignment Username: sa Password: <password>


## Demo Identity

This project uses simple seeded demo users instead of full authentication.

Requests that require a user identity use the following HTTP header:
X-User-Id: <user-id>

### Demo Teacher
User ID: T1, Role: Teacher, Name: Demo Teacher, Email: teacher@example.com, password: Demo#Teacher

### Demo Students 
User ID: S1, Role: Student, Name: Demo Student, Email: Student@example.com, password: Demo#Student
User ID: S2, Role: Student, Name: Alex Reader, Email: alex@example.com, password: Alex#Reader
User ID: S3, Role: Student, Name: Sam Learner, Email: sam@example.com, password: Sam#Learner


This approach keeps the challenge focused on the core reading assignment workflow.


## Architectural Decisions

### Spring Boot Backend

Spring Boot was chosen for the backend because it provides a productive way to build REST APIs with minimal setup.

### Layered Design

The backend is organized around domain areas:

- `book`
- `user`
- `assignment`
- `config`

Assignment-related business logic is handled in a service layer to keep controllers focused on HTTP concerns.

### H2 Database

H2 is used for local development to make the project easy to run without external dependencies.

For a production deployment, PostgreSQL or another persistent relational database would be preferred.

### Seeded Demo Data

The app seeds demo users and books at startup so reviewers can test the core workflow immediately.

### Simplified Authentication

Instead of implementing full authentication, the application uses an `X-User-Id` header to represent the current user.

This is a deliberate scope tradeoff. With more time, this would be replaced with Spring Security and JWT or OAuth-based authentication.

### External Book Content

Books include a `contentUrl` rather than storing book files directly.

This keeps the application lightweight while still allowing students to open and read assigned content.

## Tradeoffs and Assumptions

- Authentication is simplified for challenge scope.
- Demo users are seeded at startup.
- A teacher can assign one book to multiple students.
- Each assignment belongs to one student.
- Assignment progress is updated by the assigned student.
- Minutes read is calculated as the difference between the assignment start time and the assignment finish time (for completed assignments), or the current time if the assignment is in progress.
- Assignment start time is set to the current time when the assignment is created.
- Assignment finish time is set when the assignment is completed by the assigned student.
- Assignment status is set to NOT_STARTED by default.
- Assignment status is updated by the assigned student.
- Assignment statuses are fixed enum values.
- H2 is used locally for ease of setup.
- Book content is represented by external URLs.

## What I Would Improve With More Time

- Add real authentication with Spring Security and JWT
- Add a bcrypt password-based login or OAuth
- Add implementation for using a password manager like LastPass, 1Password, or secret manager
- Add frontend role-based route guards
- Add PostgreSQL for deployed persistence
- Add Docker Compose for local development
- Add pagination and filtering for assignment lists
- Add teacher comments or feedback on assignments
- Add progress history instead of storing only the latest value
- Add stronger error handling and API response models
- Add backend integration tests
- Add frontend unit and integration tests
- Add CI checks for build and tests

## Deployments

The frontend is deployed on Vercel @ https://reading-assignment-ui.vercel.app/
The backend is deployed on Render @ https://reading-assignment.onrender.com


## Repositories
Frontend: https://github.com/obiekrix/reading-assignment-ui
Backend: https://github.com/obiekrix/reading-assignment


## Review Notes

This implementation focuses on delivering a clear end-to-end workflow for assigning books and tracking reading progress.

The main simplifications are around authentication, production persistence, and UI polish. These were intentionally kept lightweight to prioritize the core product behavior within the challenge scope.
