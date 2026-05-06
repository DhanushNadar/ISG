# Blood Bank Management System 🩸

An enterprise-grade, full-stack web application designed for comprehensive tracking and management of patient blood records, health data, donation eligibility, and blood camp organization.

This system leverages a **Spring Boot 3.x backend** protected by industry-standard **JSON Web Token (JWT) Security**, and a stunning, responsive **Angular 17+** frontend.

---

## 🌟 Core Features

- **Hospital Command Center**: Hospitals can register patients, securely upload and review encrypted medical reports, and manage their local blood inventory.
- **Patient Portal**: Patients can log in using their Aadhaar Number to track their medical history, view their blood records, and actively submit medical reports.
- **Global Blood Camps**: Hospitals can organize local blood donation drives. Eligible patients can browse upcoming drives globally and seamlessly book slots with QR-styled entry slips.
- **Automated Eligibility Engine**: The system algorithmically determines a patient's donation eligibility based on their active medical diseases and dynamically prevents ineligible users from booking slots.

---

## 🏛 Architecture Overview
1. **Backend API (`/bloodbank`)**: Java 17 + Spring Boot, featuring layered REST architecture, BCrypt password hashing, custom JWT interceptors, and robust Hibernate/JPA object-relational mapping.
2. **Frontend UI (`/frontend`)**: Angular 17. Built with modern standalone components, strict Route Guards (`AuthGuard`), and a standard clinical light-mode/dark-mode aesthetic leveraging `Inter` & `Outfit` typographies.
3. **Database**: PostgreSQL (Relational Database) using automatic schema-generation.

---

## 🚀 Setup & Installation Instructions

The easiest and recommended way to run this application locally is via **Docker Compose**.

### Prerequisites
Ensure you have the following installed on your machine:
- **[Docker Desktop](https://www.docker.com/products/docker-desktop/)**

---

### Boot Up the Entire Stack

You can launch the PostgreSQL Database, the Spring Boot Backend, and the Angular Frontend all simultaneously with a single command!

1. Open your terminal and navigate to the root directory of the project:
   ```bash
   cd ISG
   ```
2. Run the following Docker Compose command to build and deploy the containers in detached mode:
   ```bash
   docker-compose up --build -d
   ```

> **What this does:**
> - Starts a `postgres` container on port `5432`.
> - Builds and starts the Java Spring Boot Backend on `http://localhost:8080`.
> - Builds and starts the Angular Frontend using NGINX on `http://localhost:80`.

**The application is now fully live! Visit [http://localhost](http://localhost) in your web browser.**

---

## 🔐 Authentication Usage

The entire system is secured behind robust Route Guards. 

### For Hospitals
1. Navigate to `http://localhost/register`. 
2. Create an account by filling out your email, creating a password, and selecting the **HOSPITAL** role. 
3. Upon registration, you will be granted an encrypted JWT Bearer Token, logging you in.
4. You may now access the `/dashboard` to organize blood camps, review patient reports, and manage the patient directory.

### For Patients
1. Ensure a Hospital has already registered you in the system (providing your Aadhaar number).
2. Visit `http://localhost/login` and select **Patient Login**.
3. Use your **Aadhaar Number** as the identifier, and the password provided to you or established during your first portal access.
4. Access the `/patient-portal` to book upcoming blood camps and submit medical recovery reports.

---

## 🔒 Security & Secrets

This repository does not store any active production secrets. 
- All database passwords in `docker-compose.yml` are strictly for local sandbox development.
- The default `JWT_SECRET` falls back to a dummy key unless provided via the `JWT_SECRET` environment variable in production.
- Third-party API keys (like Resend) must be injected safely into the container environment via `RESEND_API_KEY`.

Happy Developing! 🚀
