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

---

## 📦 Production Builds (Creating Deployable WAR / Assets)

When it's time to test your application on an external server (like Apache Tomcat or AWS), you can create compiled and bundled variations of your application!

### 1. Unified Full-Stack Archetype (Backend + Frontend together)
This command builds a single `.war` file that contains BOTH the Java Backend API securely serving the Frontend Angular HTML.
```bash
# DIR: ISG/frontend
# Compile the Angular frontend for production
npx ng build

# Copy the generated assets over to Spring Boot
# Move contents of `frontend/dist/frontend/browser` -> `bloodbank/src/main/resources/static`

# DIR: ISG/bloodbank
# Compile the unified WAR file
./mvnw clean package -DskipTests
```
Your final file will be generated at `ISG/bloodbank/target/bloodbank-0.0.1-SNAPSHOT.war`. You can deploy this exact file into Tomcat's `/webapps` directory, or immediately stand it up locally via:

```bash
# DIR: ISG/bloodbank
java "-Duser.timezone=UTC" -jar target\bloodbank-0.0.1-SNAPSHOT.war
```

### 2. Standalone Backend Build (Spring Server Only)
If you want to host your Java Backend strictly as a standalone API on a server without serving any UI files locally, you can build a backend-only WAR package.
```bash
# DIR: ISG/bloodbank
# First, ensure the 'static' folder (src/main/resources/static) is totally empty so no UI is bundled.
./mvnw clean package -DskipTests
```
This drops the same `.war` into `target/`, but without the bloat of external web pages.

### 3. Standalone Frontend Build (Angular Only)
*Note: Angular is a pure Javascript framework, so it doesn't actually produce Java `.war` files. It produces standard web output for Nginx, Vercel, or Apache.*
```bash
# DIR: ISG/frontend
npm run build
```
Your static minified files will be inside `dist/frontend/browser`. Take those `.js`, `.css`, and `index.html` files and drop them directly into your static web server (like NGINX)!

Happy Developing! 🚀
