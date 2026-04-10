# 🏠 Smart Home Advisory System

Version: 1.0  
Architecture: Monolithic  
Backend: Spring Boot  
Frontend: VueJS  
Database: PostgreSQL

--------------------------------------------------

# 1. PROJECT OVERVIEW

Smart Home Advisory System is a web-based platform designed to help users improve energy efficiency and sustainability in their homes.

The system provides intelligent suggestions (tips) to help users reduce energy consumption and optimize home usage.

Main Capabilities:

- User registration and login
- Home management
- Room management
- Device tracking
- Energy monitoring
- Smart recommendations
- Energy-saving tips
- Report generation
- Notification system

--------------------------------------------------

# 2. SYSTEM ARCHITECTURE

Architecture Style:

Monolithic (Layered + Modular)

Tech Stack:

Frontend:

- VueJS
- Vue Router
- Pinia
- Axios
- Chart.js

Backend:

- Spring Boot
- Spring Security
- JWT Authentication
- Spring Data JPA
- Maven

Database:

- PostgreSQL

Documentation Files:

- database.md
- kien_truc.md
- backend-architecture.md
- frontend-architecture.md

--------------------------------------------------

# 3. BACKEND STRUCTURE

Root Package:

com.smarthome

Structure:

com.smarthome

├── config/  
├── security/  
├── controller/  
├── service/  
├── repository/  
├── entity/  
├── dto/  
├── mapper/  
├── exception/  
├── util/

Layered Architecture:

Controller → Service → Repository → Database

Never allow:

Controller → Repository directly.

--------------------------------------------------

# 4. FRONTEND STRUCTURE

Root:

src/

Structure:

src/

├── assets/  
├── components/  
├── views/  
├── router/  
├── store/  
├── services/  
├── layouts/  
├── utils/

Architecture Type:

Component-Based Design.

--------------------------------------------------

# 5. DATABASE DESIGN

Database schema defined in:

database.md

Main Tables:

- users
- roles
- user_profiles
- homes
- rooms
- devices
- device_types
- device_usage_logs
- energy_records
- electricity_bills
- tip_categories
- tips
- recommendations
- reports
- notifications

Key Relationships:

User → Homes  
Home → Rooms  
Room → Devices  
Device → Energy Records  
Home ↔ Tips (Many-to-Many via recommendations)

--------------------------------------------------

# 6. CORE MODULES

System Modules:

1. Authentication Module
2. User Module
3. Home Module
4. Room Module
5. Device Module
6. Energy Module
7. Tip Module
8. Recommendation Module
9. Report Module
10. Notification Module

Each module must include:

- Controller
- Service
- Repository
- Entity
- DTO

--------------------------------------------------

# 7. SECURITY DESIGN

Authentication Method:

JWT (JSON Web Token)

Security Tools:

- Spring Security
- JWT Filter
- BCrypt Password Encoder

Authorization Header:

Authorization: Bearer <token>

--------------------------------------------------

# 8. STANDARD API RESPONSE FORMAT

Success Response:

{
"timestamp": "",
"status": "SUCCESS",
"message": "",
"data": {}
}

Error Response:

{
"timestamp": "",
"status": "ERROR",
"message": "",
"errors": []
}

--------------------------------------------------

# 9. DATA FLOW

Typical Flow:

User → Controller → Service → Repository → Database → Response

Example:

Create Device:

DeviceController  
→ DeviceService  
→ DeviceRepository  
→ Database

--------------------------------------------------

# 10. AI CODING RULES (STRICT)

These rules must always be followed when generating code.

--------------------------------------------------

RULE 1 — Use Layered Architecture

Always generate:

- Controller
- Service
- Repository
- Entity
- DTO

Never skip layers.

--------------------------------------------------

RULE 2 — Follow Naming Convention

Entity:

User  
Home  
Room  
Device  
Tip  
Recommendation

Controller:

UserController  
HomeController  
DeviceController

Service:

UserService  
HomeService  
DeviceService

Repository:

UserRepository  
HomeRepository  
DeviceRepository

--------------------------------------------------

RULE 3 — Use DTO Pattern

Never expose Entity directly.

Always:

Entity → DTO → Response

--------------------------------------------------

RULE 4 — Use Validation

Use annotations:

@NotNull  
@NotBlank  
@Email

--------------------------------------------------

RULE 5 — Use Transaction Management

Apply:

@Transactional

In Service Layer.

--------------------------------------------------

RULE 6 — Use Pagination

All list APIs must support pagination.

Use:

Pageable pageable

--------------------------------------------------

RULE 7 — Follow Database Schema

Always follow:

database.md

Do NOT create new tables without specification.

--------------------------------------------------

RULE 8 — Use Standard Exception Handling

Must implement:

GlobalExceptionHandler

--------------------------------------------------

RULE 9 — Use Logging

Use logging for:

- API requests
- Errors
- Warnings

--------------------------------------------------

RULE 10 — Use Clean Code Principles

Follow:

- Single Responsibility Principle
- Separation of Concerns
- Reusable components

--------------------------------------------------

# 11. RECOMMENDATION ENGINE DESIGN

Core Logic:

RecommendationService

Steps:

1. Load home data
2. Analyze device usage
3. Match tips
4. Save recommendations
5. Return results

Recommendation Status:

- PENDING
- IMPLEMENTED
- IGNORED

--------------------------------------------------

# 12. PERFORMANCE STRATEGY

Optimization Techniques:

- Database Indexing
- Pagination
- Lazy Loading
- Caching (future)

Large Data Tables:

- energy_records
- device_usage_logs

--------------------------------------------------

# 13. TESTING STRATEGY

Testing Types:

- Unit Testing
- Integration Testing

Tools:

- JUnit
- Mockito

--------------------------------------------------

# 14. BUILD INSTRUCTIONS

Backend:

Build:

mvn clean install

Run:

java -jar app.jar

Frontend:

Install:

npm install

Run:

npm run dev

Build:

npm run build

--------------------------------------------------

# 15. DEPLOYMENT MODEL

Deployment Type:

Single Monolithic Deployment.

Deployment Steps:

1. Build Backend (JAR)
2. Build Frontend
3. Configure Database
4. Run Application

Optional:

Docker deployment.

--------------------------------------------------

# 16. FUTURE EXTENSIONS

Possible Enhancements:

- Redis Caching
- Kafka Messaging
- AI Recommendation Engine
- Microservices Migration

--------------------------------------------------

# 17. IMPORTANT NOTES FOR AI

Always read these files before generating code:

1. database.md
2. backend-architecture.md
3. frontend-architecture.md
4. kien_truc.md

Never generate code outside defined architecture.

Always follow module separation.

--------------------------------------------------

# END OF README