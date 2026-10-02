# Digital Certificate Verification & Billing System

A complete Spring Boot & Web application for issuing cryptographically verifiable digital certificates (SHA-256), managing institution subscriptions, processing simulated bank billing/payments, and tracking platform P&L and unit budget reports.

---

## 🌟 Key Features

1. **Institution Management**: Register and manage educational institutions and organizations.
2. **Digital Certificate Issuance**: Issue digital certificates with sequential readable IDs (`CERT-1001`, `CERT-1002`) and tamper-proof SHA-256 cryptographic hashes.
3. **Certificate Verification**: Instant verification of any certificate using its SHA-256 hash. Returns verified details or invalid status card.
4. **Billing & Settlement**: Generate billing invoices for API queries or credentials. Process simulated instant settlements using `BANK` payment method.
5. **Platform Analytics & Reports**:
   - **Platform P&L**: Tracks total revenue (PAID invoices), settlements received, and net profit.
   - **Unit Budget**: Automated revenue allocation breakdown between Certificate Verification services and API Billing.

---

## 🛠️ Technology Stack

- **Backend Framework**: Java 21, Spring Boot 3.3.4
- **Persistence & Data**: Spring Data JPA, Hibernate, MySQL Database (`certificate_db`)
- **Utility**: Java `MessageDigest` (SHA-256 Hashing)
- **Frontend**: HTML5, Vanilla CSS3 (Custom Responsive Dashboard), Vanilla JavaScript (`fetch`, `async/await`)
- **Build Tool**: Maven

---

## 📁 Project Structure

```
src
└── main
    ├── java
    │   └── com.example.certificate
    │       │
    │       ├── CertificateApplication.java
    │       │
    │       ├── controller
    │       │   ├── InstitutionController.java
    │       │   ├── CertificateController.java
    │       │   ├── InvoiceController.java
    │       │   ├── PaymentController.java
    │       │   └── ReportController.java
    │       │
    │       ├── service
    │       │   ├── InstitutionService.java
    │       │   ├── CertificateService.java
    │       │   ├── InvoiceService.java
    │       │   ├── PaymentService.java
    │       │   └── ReportService.java
    │       │
    │       ├── repository
    │       │   ├── InstitutionRepository.java
    │       │   ├── CertificateRepository.java
    │       │   ├── InvoiceRepository.java
    │       │   └── PaymentRepository.java
    │       │
    │       ├── model
    │       │   ├── Institution.java
    │       │   ├── Certificate.java
    │       │   ├── Invoice.java
    │       │   └── Payment.java
    │       │
    │       └── util
    │           └── HashUtil.java
    │
    └── resources
        ├── application.properties
        │
        └── static
            ├── index.html
            ├── style.css
            └── script.js
```

---

## 🛢️ Database Setup (MySQL)

1. Make sure MySQL server is running on `localhost:3306`.
2. Open `src/main/resources/application.properties` and configure your database credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/certificate_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

> **Note**: Database schema (`certificate_db`) and tables (`institutions`, `certificates`, `invoices`, `payments`) will be created automatically on application startup via `spring.jpa.hibernate.ddl-auto=update`.

---

## 🚀 How to Run

### Method 1: From IntelliJ IDEA (Recommended)
1. Open the project folder in **IntelliJ IDEA**.
2. Wait for Maven to import dependencies.
3. Locate `src/main/java/com/example/certificate/CertificateApplication.java`.
4. Click the green **Run** button next to `main()`.

### Method 2: Command Line (Maven Wrapper)
```bash
./mvnw clean spring-boot:run
```
*(On Windows Command Prompt / PowerShell: `mvnw spring-boot:run`)*

---

## 🌐 Frontend Application URL

Access the interactive web dashboard at:
[http://localhost:8080/](http://localhost:8080/)

---

## 📡 REST API Endpoints & Sample Requests

### 1. Institutions
- **Register Institution**: `POST /api/institutions`
  ```json
  {
    "name": "ABC Engineering College",
    "email": "abc@gmail.com",
    "address": "Chennai"
  }
  ```
- **Get All Institutions**: `GET /api/institutions`
- **Get Institution by ID**: `GET /api/institutions/{id}`

### 2. Certificates
- **Issue Certificate**: `POST /api/certificates`
  ```json
  {
    "studentName": "Gokul",
    "course": "B.Tech Information Technology",
    "institutionId": 1
  }
  ```
- **Get All Certificates**: `GET /api/certificates`
- **Verify Certificate**: `GET /api/certificates/verify/{hash}`

### 3. Invoices
- **Create Invoice**: `POST /api/invoices`
  ```json
  {
    "institutionId": 1,
    "packageName": "100 API Queries",
    "amount": 500
  }
  ```
- **Get All Invoices**: `GET /api/invoices`
- **Get Invoice by ID**: `GET /api/invoices/{id}`

### 4. Payments
- **Make Payment**: `POST /api/payments`
  ```json
  {
    "invoiceId": 1,
    "paymentMethod": "BANK"
  }
  ```
- **Get All Payments**: `GET /api/payments`

### 5. Reports
- **Platform P&L Report**: `GET /api/reports/pl`
- **Unit Budget Report**: `GET /api/reports/budget`
