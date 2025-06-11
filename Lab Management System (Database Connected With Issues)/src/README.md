# Lab Management System

A comprehensive system for managing computer labs, handling complaints, software requests, and generating reports.

## System Architecture

The system follows a three-tier architecture:
1. **Interface Tier**: JavaFX UI components
2. **Application Tier**: Controller/Facade pattern that mediates between UI and data
3. **Data Tier**: Database layer using SQLite for persistent storage

## Database Structure

The database schema includes tables for:
- Users (Lab Coordinators, Teachers, Students)
- Labs
- Computers
- Hardware details
- Software installations
- Complaints
- Software requests
- Reports

## Setup and Running

### Prerequisites
- Java JDK 8 or higher
- Windows OS (for batch files)

### Steps to Run

1. **Download Dependencies**:
   Run the `download_dependencies.bat` script to download the SQLite JDBC driver:
   ```
   download_dependencies.bat
   ```

2. **Compile the Application**:
   Run the `compile.bat` script to compile all Java files:
   ```
   compile.bat
   ```

3. **Run the Application**:
   Run the `run.bat` script to start the application:
   ```
   run.bat
   ```

## Features

- User authentication (login/signup)
- Register and track complaints
- Request software installation
- Manage computer hardware details
- Generate lab and hardware reports
- Track installed software
- Approve or reject requests

## Persistence

User data, complaints, and other information are stored in an SQLite database (`lms.db`) that persists between application sessions. When you create a new user account, it will be available for login even after restarting the application.

## Technical Notes

- The application uses SQLite for data storage
- JDBC is used for database connectivity
- The Controller class acts as a Facade between UI and data tiers
- Reflection is used in some cases to set fields directly 