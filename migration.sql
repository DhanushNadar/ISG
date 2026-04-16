-- Blood Bank Management System: PostgreSQL Enterprise Migration Script
-- Purpose: Schema Initialization for Dedicated Production Servers
-- Note: Execute this file against your clean database (e.g. 'bloodbank')

-- 1. DROPS (Run carefully, removes existing tables)
DROP TABLE IF EXISTS patient_diseases CASCADE;
DROP TABLE IF EXISTS blood_records CASCADE;
DROP TABLE IF EXISTS diseases CASCADE;
DROP TABLE IF EXISTS patients CASCADE;
DROP TABLE IF EXISTS hospitals CASCADE;
DROP TABLE IF EXISTS site_users CASCADE;

-- 2. CREATE TABLES

-- Users / Authentication
CREATE TABLE site_users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- Core Entities
CREATE TABLE patients (
    id BIGSERIAL PRIMARY KEY,
    aadhaar_number VARCHAR(12) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    age INTEGER,
    gender VARCHAR(50),
    blood_group VARCHAR(10),
    phone VARCHAR(20),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE hospitals (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    contact_number VARCHAR(50)
);

CREATE TABLE diseases (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    is_major BOOLEAN NOT NULL,
    description TEXT
);

-- Relational Tables (Foreign Keys)
CREATE TABLE patient_diseases (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    disease_id BIGINT NOT NULL,
    diagnosed_date DATE,
    status VARCHAR(50),
    is_current BOOLEAN NOT NULL,
    CONSTRAINT fk_pd_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    CONSTRAINT fk_pd_disease FOREIGN KEY (disease_id) REFERENCES diseases(id) ON DELETE CASCADE
);

CREATE TABLE blood_records (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    hospital_id BIGINT NOT NULL,
    donation_date DATE NOT NULL,
    wbc DOUBLE PRECISION,
    rbc DOUBLE PRECISION,
    hemoglobin DOUBLE PRECISION,
    platelets DOUBLE PRECISION,
    cholesterol DOUBLE PRECISION,
    CONSTRAINT fk_br_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    CONSTRAINT fk_br_hospital FOREIGN KEY (hospital_id) REFERENCES hospitals(id) ON DELETE CASCADE
);

-- 3. INITIAL SEED DATA (Optional)
-- Insert standard diseases
INSERT INTO diseases (name, is_major, description) VALUES 
('Anemia', false, 'Condition in which you lack enough healthy red blood cells.'),
('Leukemia', true, 'Cancer of the body''s blood-forming tissues, including the bone marrow and the lymphatic system.'),
('HIV/AIDS', true, 'Chronic, potentially life-threatening condition caused by the human immunodeficiency virus (HIV).'),
('Malaria', false, 'A disease caused by a plasmodium parasite, transmitted by the bite of infected mosquitoes.'),
('Dengue', true, 'Mosquito-borne viral disease occurring in tropical and subtropical areas.'),
('Hepatitis B', true, 'A serious liver infection caused by the hepatitis B virus that''s easily preventable by a vaccine.');

-- Insert default admin/hospital account (Password is 'password', hashed using BCrypt)
INSERT INTO site_users (email, password, role) VALUES 
('admin@bloodbank.local', '$2a$10$wYQj0zV24v2qC0w12s/hYucw8t8Y9.yvT41T6uT4D8x3V9sBqFv.y', 'HOSPITAL');
