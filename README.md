# 📦 Logistics Company Management System

A comprehensive web-based application designed to manage the core processes of a logistics company. The system facilitates the reception, tracking, and delivery of shipments, streamlining communication between office employees, couriers, and clients.

## 🚀 Overview

The "Logistics Company" application is a responsive web platform that manages the lifecycle of package delivery. It supports multiple user roles, dynamic pricing based on shipment details, and extensive reporting capabilities for management.

## 🛠️ Tech Stack

This project is built using the following technologies:

* **Backend:** Java 17, Spring Boot 3.2.0
* **Database:** Microsoft SQL Server
* **Frontend / Templating:** Thymeleaf (Server-side rendering), HTML5, CSS3
* **Security:** Spring Security 6
* **ORM:** Spring Data JPA (Hibernate)
* **Build Tool:** Gradle
* **Utilities:** Lombok, Spring Validation

## ✨ Key Features

The system implements the following functional requirements:

### 👥 User Roles & Access Control
* **Administrator:** Full CRUD access to company data, offices, and employee management.
* **Office Employee:** Handles client requests and package registration in specific offices.
* **Courier:** Manages package deliveries to physical addresses.
* **Client:** Can track sent/received packages and view personal history.

### 📦 Package Management
* **Registration:** Create new shipments defining sender, receiver, weight, and destination.
* **Delivery Types:** Supports delivery to a company **Office** (lower cost) or a specific **Address**.
* **Pricing Logic:** Automatic shipping cost calculation based on package weight and delivery type.
* **Status Tracking:** Monitor packages (e.g., Registered, In Transit, Delivered).

### 🏢 Resource Management (CRUD)
* Manage **Offices** and locations.
* Manage **Employees** and role assignments.
* Manage **Clients** and user accounts.

### 📊 Reporting & Analytics
* Generate reports for all employees and clients.
* View all registered shipments.
* Filter shipments by specific employee registration.
* Track packages sent but not yet received.
* Financial reports: View company income over a specific period.

## ⚙️ Installation & Setup

### Prerequisites
* Java Development Kit (JDK) 17 or higher.
* Microsoft SQL Server installed and running.

### Configuration
1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/your-username/logistics-company.git](https://github.com/your-username/logistics-company.git)
    cd logistics-company
    ```

2.  **Database Setup:**
    Open `src/main/resources/application.properties` and configure your MS SQL Server credentials:
    ```properties
    spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=LogisticsCompany;encrypt=true;trustServerCertificate=true
    spring.datasource.username=YOUR_DB_USERNAME
    spring.datasource.password=YOUR_DB_PASSWORD
    ```

3.  **Run the Application:**
    Using Gradle Wrapper:
    ```bash
    ./gradlew bootRun
    ```

4.  **Access the App:**
    Open your browser and navigate to: `http://localhost:8080`

## 🧪 Testing

The project includes unit tests for core services and security configurations. To run the tests:

```bash
./gradlew test
