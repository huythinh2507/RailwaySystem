-- Railway System Database Setup Script - MySQL VERSION
-- This script creates the database, tables, and seeds them with sample data

-- ========================================
-- DATABASE CREATION
-- ========================================

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS railway_system;

-- Use the database
USE railway_system;

-- ========================================
-- TABLE CREATION
-- ========================================

-- Drop existing tables if they exist (in reverse order of dependencies)
DROP TABLE IF EXISTS Admin_Train;
DROP TABLE IF EXISTS Schedule;
DROP TABLE IF EXISTS Ticket;
DROP TABLE IF EXISTS Admin_Station;
DROP TABLE IF EXISTS Station;
DROP TABLE IF EXISTS Train;
DROP TABLE IF EXISTS Passenger;
DROP TABLE IF EXISTS Login;
DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Admin;

-- Create User table
CREATE TABLE User (
    Username VARCHAR(50) PRIMARY KEY NOT NULL,
    Name VARCHAR(100) NOT NULL,
    Address VARCHAR(255),
    City VARCHAR(80),
    Age INT,
    Contact VARCHAR(15),
    Gender CHAR(1)
);

-- Create Login table
CREATE TABLE Login (
    Username VARCHAR(50) PRIMARY KEY NOT NULL,
    Password VARCHAR(255) NOT NULL,
    FOREIGN KEY (Username) REFERENCES User(Username) ON DELETE CASCADE
);

-- Create Passenger table
CREATE TABLE Passenger (
    PNR VARCHAR(10) PRIMARY KEY NOT NULL,
    PassengerName VARCHAR(100) NOT NULL,
    Age INT,
    Gender CHAR(1),
    Source VARCHAR(100),
    Destination VARCHAR(100),
    Username VARCHAR(50) NOT NULL,
    FOREIGN KEY (Username) REFERENCES User(Username) ON DELETE CASCADE
);

-- Create Train table
CREATE TABLE Train (
    TrainNumber VARCHAR(10) PRIMARY KEY NOT NULL,
    TrainName VARCHAR(100) NOT NULL,
    Source VARCHAR(100),
    Destination VARCHAR(100),
    Date DATE,
    Cost DECIMAL(10,2)
);

-- Create Ticket table
CREATE TABLE Ticket (
    PNR VARCHAR(10) NOT NULL,
    TrainNumber VARCHAR(10) NOT NULL,
    Date DATE,
    Source VARCHAR(100),
    Destination VARCHAR(100),
    PRIMARY KEY (PNR, TrainNumber),
    FOREIGN KEY (PNR) REFERENCES Passenger(PNR) ON DELETE CASCADE,
    FOREIGN KEY (TrainNumber) REFERENCES Train(TrainNumber) ON DELETE CASCADE
);

-- Create Admin table
CREATE TABLE Admin (
    AdminID INT PRIMARY KEY NOT NULL,
    Address VARCHAR(255)
);

-- Create Station table
CREATE TABLE Station (
    StationName VARCHAR(100) PRIMARY KEY NOT NULL,
    Address VARCHAR(255)
);

-- Create Admin_Station table
CREATE TABLE Admin_Station (
    AdminID INT NOT NULL,
    StationName VARCHAR(100) NOT NULL,
    PRIMARY KEY (AdminID, StationName),
    FOREIGN KEY (AdminID) REFERENCES Admin(AdminID) ON DELETE CASCADE,
    FOREIGN KEY (StationName) REFERENCES Station(StationName) ON DELETE CASCADE
);

-- Create Schedule table
CREATE TABLE Schedule (
    TrainNumber VARCHAR(10) NOT NULL,
    StationName VARCHAR(100) NOT NULL,
    ScheduleID VARCHAR(10),
    arrd_arrival_time TIME,
    dept_departure_time TIME,
    PRIMARY KEY (TrainNumber, StationName),
    FOREIGN KEY (TrainNumber) REFERENCES Train(TrainNumber) ON DELETE CASCADE,
    FOREIGN KEY (StationName) REFERENCES Station(StationName) ON DELETE CASCADE
);

-- Create Admin_Train table
CREATE TABLE Admin_Train (
    AdminID INT NOT NULL,
    TrainNumber VARCHAR(10) NOT NULL,
    PRIMARY KEY (AdminID, TrainNumber),
    FOREIGN KEY (AdminID) REFERENCES Admin(AdminID) ON DELETE CASCADE,
    FOREIGN KEY (TrainNumber) REFERENCES Train(TrainNumber) ON DELETE CASCADE
);

-- ========================================
-- SEED DATA
-- ========================================

-- Insert sample Users
INSERT INTO User (Username, Name, Address, City, Age, Contact, Gender) VALUES
('john_doe', 'John Doe', '123 Main St', 'New York', 30, '555-0101', 'M'),
('jane_smith', 'Jane Smith', '456 Oak Ave', 'Los Angeles', 28, '555-0102', 'F'),
('bob_wilson', 'Bob Wilson', '789 Pine Rd', 'Chicago', 35, '555-0103', 'M'),
('alice_brown', 'Alice Brown', '321 Elm St', 'Houston', 32, '555-0104', 'F'),
('charlie_davis', 'Charlie Davis', '654 Maple Dr', 'Phoenix', 29, '555-0105', 'M');

-- Insert sample Login credentials
INSERT INTO Login (Username, Password) VALUES
('john_doe', 'password123'),
('jane_smith', 'securepass456'),
('bob_wilson', 'bobpass789'),
('alice_brown', 'alicepass012'),
('charlie_davis', 'charliepass345');

-- Insert sample Stations
INSERT INTO Station (StationName, Address) VALUES
('Grand Central Station', '89 E 42nd St, New York, NY 10017'),
('Union Station LA', '800 N Alameda St, Los Angeles, CA 90012'),
('Chicago Union Station', '225 S Canal St, Chicago, IL 60606'),
('Houston Central', '902 Washington Ave, Houston, TX 77002'),
('Phoenix Station', '401 W Harrison St, Phoenix, AZ 85003');

-- Insert sample Trains
INSERT INTO Train (TrainNumber, TrainName, Source, Destination, Date, Cost) VALUES
('T001', 'Express East', 'New York', 'Chicago', '2025-11-25', 89.99),
('T002', 'Western Flyer', 'Los Angeles', 'Phoenix', '2025-11-26', 65.50),
('T003', 'Southern Cross', 'Chicago', 'Houston', '2025-11-27', 112.75),
('T004', 'Coast to Coast', 'New York', 'Los Angeles', '2025-11-28', 245.00),
('T005', 'Midwest Express', 'Chicago', 'Phoenix', '2025-11-29', 135.25);

-- Insert sample Passengers
INSERT INTO Passenger (PNR, PassengerName, Age, Gender, Source, Destination, Username) VALUES
('PNR001', 'John Doe', 30, 'M', 'New York', 'Chicago', 'john_doe'),
('PNR002', 'Jane Smith', 28, 'F', 'Los Angeles', 'Phoenix', 'jane_smith'),
('PNR003', 'Bob Wilson', 35, 'M', 'Chicago', 'Houston', 'bob_wilson'),
('PNR004', 'Alice Brown', 32, 'F', 'New York', 'Los Angeles', 'alice_brown'),
('PNR005', 'Charlie Davis', 29, 'M', 'Chicago', 'Phoenix', 'charlie_davis');

-- Insert sample Tickets
INSERT INTO Ticket (PNR, TrainNumber, Date, Source, Destination) VALUES
('PNR001', 'T001', '2025-11-25', 'New York', 'Chicago'),
('PNR002', 'T002', '2025-11-26', 'Los Angeles', 'Phoenix'),
('PNR003', 'T003', '2025-11-27', 'Chicago', 'Houston'),
('PNR004', 'T004', '2025-11-28', 'New York', 'Los Angeles'),
('PNR005', 'T005', '2025-11-29', 'Chicago', 'Phoenix');

-- Insert sample Admins
INSERT INTO Admin (AdminID, Address) VALUES
(1, '100 Admin Plaza, New York, NY'),
(2, '200 Management Ave, Los Angeles, CA'),
(3, '300 Executive St, Chicago, IL');

-- Insert sample Admin_Station relationships
INSERT INTO Admin_Station (AdminID, StationName) VALUES
(1, 'Grand Central Station'),
(1, 'Chicago Union Station'),
(2, 'Union Station LA'),
(2, 'Phoenix Station'),
(3, 'Chicago Union Station'),
(3, 'Houston Central');

-- Insert sample Schedules
INSERT INTO Schedule (TrainNumber, StationName, ScheduleID, arrd_arrival_time, dept_departure_time) VALUES
('T001', 'Grand Central Station', 'SCH001', '08:00:00', '08:30:00'),
('T001', 'Chicago Union Station', 'SCH002', '16:00:00', '16:30:00'),
('T002', 'Union Station LA', 'SCH003', '09:00:00', '09:30:00'),
('T002', 'Phoenix Station', 'SCH004', '14:00:00', '14:30:00'),
('T003', 'Chicago Union Station', 'SCH005', '10:00:00', '10:30:00'),
('T003', 'Houston Central', 'SCH006', '18:00:00', '18:30:00');

-- Insert sample Admin_Train relationships
INSERT INTO Admin_Train (AdminID, TrainNumber) VALUES
(1, 'T001'),
(1, 'T004'),
(2, 'T002'),
(3, 'T003'),
(3, 'T005');

-- Display confirmation message
SELECT 'Database setup completed successfully!' AS Message;
SELECT 'Tables created: User, Login, Passenger, Train, Ticket, Admin, Station, Admin_Station, Schedule, Admin_Train' AS Info;
SELECT 'Sample data has been inserted into all tables.' AS Info;
