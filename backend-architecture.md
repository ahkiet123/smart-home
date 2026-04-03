# BACKEND ARCHITECTURE — SMART HOME SYSTEM

Version: 1.0  
Architecture Style: Monolithic (Layered + Modular)  
Framework: Spring Boot  
Language: Java  
ORM: Spring Data JPA  
Security: Spring Security + JWT  
Database: PostgreSQL

--------------------------------------------------

# 1. BACKEND OVERVIEW

This backend system is implemented as a monolithic Spring Boot application.

All modules are developed inside a single deployable unit.

Architecture Type:

Layered Monolithic Architecture with modular separation.

Main Responsibilities:

- Handle REST API requests
- Process business logic
- Access database
- Manage authentication
- Generate recommendations
- Track energy usage

--------------------------------------------------

# 2. BACKEND LAYERED STRUCTURE

The system follows 5 layers:

1. Controller Layer
2. Service Layer
3. Repository Layer
4. Entity Layer
5. Database Layer

--------------------------------------------------

# 3. PROJECT PACKAGE STRUCTURE

Root Package:

com.smarthome

Structure:

com.smarthome

├── config/  
│       WebConfig  
│       CorsConfig  
│       SwaggerConfig

├── security/  
│       SecurityConfig  
│       JwtAuthenticationFilter  
│       JwtTokenProvider

├── controller/  
│       AuthController  
│       UserController  
│       HomeController  
│       RoomController  
│       DeviceController  
│       TipController  
│       RecommendationController  
│       ReportController  
│       NotificationController

├── service/  
│       AuthService  
│       UserService  
│       HomeService  
│       RoomService  
│       DeviceService  
│       EnergyService  
│       TipService  
│       RecommendationService  
│       ReportService  
│       NotificationService

├── repository/  
│       UserRepository  
│       HomeRepository  
│       RoomRepository  
│       DeviceRepository  
│       TipRepository  
│       RecommendationRepository  
│       ReportRepository  
│       NotificationRepository

├── entity/  
│       User  
│       Role  
│       UserProfile  
│       Home  
│       Room  
│       Device  
│       DeviceType  
│       DeviceUsageLog  
│       EnergyRecord  
│       ElectricityBill  
│       Tip  
│       TipCategory  
│       Recommendation  
│       Report  
│       Notification

├── dto/  
│       UserDTO  
│       HomeDTO  
│       DeviceDTO  
│       RecommendationDTO  
│       ReportDTO

├── mapper/  
│       UserMapper  
│       DeviceMapper  
│       HomeMapper

├── exception/  
│       GlobalExceptionHandler  
│       ResourceNotFoundException  
│       ValidationException

├── util/  
│       DateUtil  
│       EnergyCalculator

└── SmartHomeApplication.java

--------------------------------------------------

# 4. CONTROLLER LAYER

Responsibilities:

- Receive HTTP requests
- Validate request input
- Call service layer
- Return JSON responses

Common Features:

- REST APIs
- Request Mapping
- Response Handling

Example Controllers:

AuthController:

Handles:

- Register
- Login
- Token generation

UserController:

Handles:

- Update profile
- Get user info

HomeController:

Handles:

- Create home
- Update home
- List homes

DeviceController:

Handles:

- Add device
- Update device
- Delete device

RecommendationController:

Handles:

- Generate recommendation
- Update recommendation status

--------------------------------------------------

# 5. SERVICE LAYER

Responsibilities:

- Business logic processing
- Rule validation
- Data transformation
- Recommendation generation

Example Services:

AuthService:

- Register user
- Login user
- Password validation

HomeService:

- Add home
- Update home

DeviceService:

- Manage devices

EnergyService:

- Calculate energy usage

RecommendationService:

Core logic:

- Analyze home data
- Match tips
- Generate recommendations

ReportService:

- Calculate saving percentage
- Generate reports

--------------------------------------------------

# 6. REPOSITORY LAYER

Technology:

Spring Data JPA

Responsibilities:

- Database access
- Query execution
- CRUD operations

Repositories:

UserRepository  
HomeRepository  
RoomRepository  
DeviceRepository  
TipRepository  
RecommendationRepository  
ReportRepository  
NotificationRepository

Example:

UserRepository:

extends JpaRepository<User, Long>

--------------------------------------------------

# 7. ENTITY LAYER

Represents database tables.

Each entity maps to one table.

Example Entities:

User  
Home  
Room  
Device  
Tip  
Recommendation  
EnergyRecord

Entity Responsibilities:

- Define table structure
- Define relationships
- Define constraints

Example Relationship Types:

@OneToMany  
@ManyToOne  
@ManyToMany

--------------------------------------------------

# 8. SECURITY ARCHITECTURE

Authentication Method:

JWT (JSON Web Token)

Components:

SecurityConfig:

- Configure authentication rules

JwtAuthenticationFilter:

- Validate JWT token

JwtTokenProvider:

- Generate tokens

Password Encoder:

BCryptPasswordEncoder

Flow:

Login → Generate JWT → Validate JWT → Authorize Request

--------------------------------------------------

# 9. DATA VALIDATION

Validation Tool:

Jakarta Validation (Bean Validation)

Examples:

@NotNull  
@NotBlank  
@Email

Validation occurs:

- Before saving data
- Before processing requests

--------------------------------------------------

# 10. EXCEPTION HANDLING

Global Exception Handler:

GlobalExceptionHandler

Handles:

- Validation errors
- Resource not found
- Internal errors

Returns:

Standard error response format.

--------------------------------------------------

# 11. LOGGING SYSTEM

Logging Tool:

Spring Boot Logging

Log Types:

- INFO
- WARN
- ERROR

Logged Events:

- Login attempts
- Errors
- System events

--------------------------------------------------

# 12. TRANSACTION MANAGEMENT

Managed by:

Spring Transaction Manager

Annotation:

@Transactional

Used In:

Service Layer.

--------------------------------------------------

# 13. CONFIGURATION MANAGEMENT

Configuration Files:

application.yml

Contains:

- Database config
- JWT config
- Logging config

Example:

spring.datasource.url  
spring.jpa.hibernate.ddl-auto

--------------------------------------------------

# 14. PERFORMANCE OPTIMIZATION

Techniques:

- Pagination
- Lazy Loading
- Database Indexing
- Query Optimization

Tools:

Pageable  
Slice  
Stream

--------------------------------------------------

# 15. RECOMMENDATION ENGINE DESIGN

Core Module:

RecommendationService

Processing Steps:

1. Load home data
2. Analyze devices
3. Match tips
4. Save recommendations
5. Return result

Algorithm Type:

Rule-based Recommendation.

Future Upgrade:

AI-based recommendation.

--------------------------------------------------

# 16. API RESPONSE FORMAT

Standard Response:

{
"timestamp": "",
"status": "",
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

# 17. MODULE DEPENDENCY FLOW

Controller → Service → Repository → Database

Never:

Controller → Repository directly.

--------------------------------------------------

# 18. DEPLOYMENT MODEL

Deployment Type:

Single JAR deployment.

Build Tool:

Maven

Build Command:

mvn clean install

Run Command:

java -jar app.jar

--------------------------------------------------

# 19. FUTURE BACKEND EXTENSIONS

Optional Additions:

Redis:

- Caching

Kafka:

- Event streaming

AI Engine:

- Smart prediction

Scheduler:

- Daily calculations

--------------------------------------------------

# 20. NON-FUNCTIONAL REQUIREMENTS

Supported:

✔ Maintainability  
✔ Scalability  
✔ Security  
✔ Performance  
✔ Reliability

--------------------------------------------------

# END OF BACKEND ARCHITECTURE