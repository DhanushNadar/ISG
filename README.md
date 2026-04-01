# Blood Bank Management System 🩸

An enterprise-grade, full-stack web application designed for comprehensive tracking and management of patient blood records, health data, and donation eligibility. 

This system leverages a **Spring Boot 3.x backend** protected by industry-standard **JSON Web Token (JWT) Security**, and a stunning, responsive **Angular 17+** frontend.

---

## 🏛 Architecture Overview
1. **Backend API (`/bloodbank`)**: Java 17 + Spring Boot, featuring layered REST architecture, BCrypt password hashing, custom JWT interceptors, and robust Hibernate/JPA object-relational mapping.
2. **Frontend UI (`/frontend`)**: Angular 17. Built with modern standalone components, strict Route Guards (`AuthGuard`), and a standard clinical light-mode aesthetic leveraging `Inter` & `Outfit` typographies.
3. **Database**: PostgreSQL (Relational Database) using automatic schema-generation.

---

## 🚀 Setup & Installation Instructions

Follow these exact steps from top to bottom to bootstrap the system locally on your machine.

### Prerequisites Tools
Ensure you have the following installed on your machine:
- **[Docker Desktop](https://www.docker.com/products/docker-desktop/)** (for quickly running the PostgreSQL database)
- **[Java 17 JDK](https://adoptium.net/)**
- **[Node.js (v18+)](https://nodejs.org/)**

---

### Step 1: Start PostgreSQL via Docker
The Spring Boot backend fundamentally requires a PostgreSQL database named `bloodbank` running on Port `5432`. 

Open your terminal and run this single Docker command to spin up the database with the exact credentials expected by the application:

```bash
docker run --name pg-bloodbank -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=bloodbank -p 5432:5432 -d postgres
```
> **Note:** If you stop the container, you can restart it anytime using `docker start pg-bloodbank`.

---

### Step 2: Boot Up the Spring Java Backend
Once the database container is running, start the Spring REST API framework.

1. Open a new terminal.
2. Navigate into the backend folder: 
   ```bash
   cd bloodbank
   ```
3. Use the integrated Maven Wrapper to compile and boot the Java server:
   ```bash
   ./mvnw spring-boot:run
   ```
> The server will start on `http://localhost:8080`.
> *Because `spring.jpa.hibernate.ddl-auto=create-drop` is enabled, the backend will automatically create table schemas and inject a suite of Sample Patients and Diseases every time you run it.*

---

### Step 3: Serve the Angular Frontend
With the backend safely online, connect the UI.

1. Open a **second** independent terminal.
2. Navigate into the frontend workspace:
   ```bash
   cd frontend
   ```
3. Install the NPM dependencies (First time only):
   ```bash
   npm install
   ```
4. Start the Angular Dev Server:
   ```bash
   npx ng serve
   ```
> The application is now fully live! Visit **[http://localhost:4200](http://localhost:4200)** in your web browser.

---

## 🔐 Authentication Usage
The entire system is secured behind robust Route Guards. 

1. On your first visit, navigate to `http://localhost:4200/register`. 
2. Create an account by filling out your email, creating a password, and selecting the **HOSPITAL** role. 
3. Upon registration, you will be granted an encrypted JWT Bearer Token, logging you in.
4. You may now access the `/dashboard` to view statistics and root into global `/patients`.

*Happy Developing!* 🚀
