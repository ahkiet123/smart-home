# SYSTEM ARCHITECTURE — SMART HOME ADVISORY SYSTEM

Version: 1.0  
Architecture Type: Monolithic (Layered Architecture)  
Backend: Spring Boot  
Frontend: VueJS  
Database: PostgreSQL

--------------------------------------------------

# 1. SYSTEM OVERVIEW

The Smart Home Advisory System is a web-based application designed to help users improve energy efficiency and sustainability in their homes.

The system allows users to:

- Register and manage accounts
- Add homes and rooms
- Manage devices
- Track energy usage
- Receive smart recommendations
- View energy-saving tips
- Generate reports
- Receive notifications

Architecture Style:

Layered Monolithic Architecture.

All modules are deployed in a single Spring Boot application.

--------------------------------------------------

# 2. HIGH-LEVEL SYSTEM ARCHITECTURE

Users interact with the system through a web browser.

System Components:

Client (VueJS)  
Backend (Spring Boot Monolith)  
Database (PostgreSQL)

--------------------------------------------------

Architecture Flow:

User → VueJS → REST API → Spring Boot → PostgreSQL

--------------------------------------------------

# 3. ARCHITECTURE DIAGRAM (HIGH LEVEL)

            USER
              │
              ▼
        ┌────────────────────┐
        │     VueJS UI       │
        │  Web Application   │
        └─────────┬──────────┘
                  │ REST API
                  ▼
        ┌────────────────────┐
        │   Spring Boot App  │
        │     (Monolith)     │
        └─────────┬──────────┘
                  │
                  ▼
        ┌────────────────────┐
        │    PostgreSQL DB   │
        └────────────────────┘

--------------------------------------------------

# 4. LAYERED ARCHITECTURE

The backend follows a layered architecture.

Layers:

1. Controller Layer
2. Service Layer
3. Repository Layer
4. Database Layer

--------------------------------------------------

# 4.1 Controller Layer

Handles incoming HTTP requests.

Responsibilities:

- Receive REST requests
- Validate input
- Call services
- Return responses

Main Controllers:

- AuthController
- UserController
- HomeController
- RoomController
- DeviceController
- TipController
- RecommendationController
- ReportController
- NotificationController

--------------------------------------------------

# 4.2 Service Layer

Handles business logic.

Responsibilities:

- Process system logic
- Apply rules
- Coordinate repositories
- Generate recommendations

Main Services:

- AuthService
- UserService
- HomeService
- RoomService
- DeviceService
- EnergyService
- TipService
- RecommendationService
- ReportService
- NotificationService

--------------------------------------------------

# 4.3 Repository Layer

Handles database operations.

Technology:

Spring Data JPA

Responsibilities:

- CRUD operations
- Query execution
- Data persistence

Main Repositories:

- UserRepository
- HomeRepository
- RoomRepository
- DeviceRepository
- TipRepository
- RecommendationRepository
- ReportRepository
- NotificationRepository

--------------------------------------------------

# 4.4 Database Layer

Technology:

PostgreSQL

Responsibilities:

- Store application data
- Maintain relationships
- Support analytics queries

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
- tips
- recommendations
- reports
- notifications

--------------------------------------------------

# 5. MODULE STRUCTURE

The monolithic backend is divided into logical modules.

Modules:

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

--------------------------------------------------

# 5.1 Authentication Module

Responsibilities:

- User login
- User registration
- JWT generation
- Token validation

Components:

- AuthController
- AuthService
- JwtUtil
- SecurityConfig

--------------------------------------------------

# 5.2 User Module

Responsibilities:

- Manage user profiles
- Update user information

Tables Used:

- users
- user_profiles

--------------------------------------------------

# 5.3 Home Module

Responsibilities:

- Add homes
- Update home details
- View home list

Tables Used:

- homes

--------------------------------------------------

# 5.4 Room Module

Responsibilities:

- Add rooms
- Manage room details

Tables Used:

- rooms

--------------------------------------------------

# 5.5 Device Module

Responsibilities:

- Add devices
- Update devices
- Track device models

Tables Used:

- devices
- device_types

--------------------------------------------------

# 5.6 Energy Module

Responsibilities:

- Track device usage
- Record energy consumption

Tables Used:

- device_usage_logs
- energy_records
- electricity_bills

--------------------------------------------------

# 5.7 Tip Module

Responsibilities:

- Store smart tips
- Categorize tips

Tables Used:

- tips
- tip_categories

--------------------------------------------------

# 5.8 Recommendation Module

Responsibilities:

- Generate recommendations
- Track recommendation status

Tables Used:

- recommendations

--------------------------------------------------

# 5.9 Report Module

Responsibilities:

- Generate energy reports
- Calculate savings

Tables Used:

- reports

--------------------------------------------------

# 5.10 Notification Module

Responsibilities:

- Send notifications
- Mark messages as read

Tables Used:

- notifications

--------------------------------------------------

# 6. FRONTEND ARCHITECTURE

Frontend Technology:

VueJS

Responsibilities:

- UI rendering
- API communication
- User interaction

Frontend Structure:

src/

views/

- LoginView
- DashboardView
- HomeView
- RoomView
- DeviceView
- TipView
- RecommendationView
- ReportView

components/

- Navbar
- Sidebar
- DeviceCard
- TipCard

services/

- api.js
- authService.js
- homeService.js

router/

- index.js

store/

- userStore.js
- homeStore.js

--------------------------------------------------

# 7. SECURITY ARCHITECTURE

Authentication Method:

JWT (JSON Web Token)

Flow:

User Login → Validate Credentials → Generate JWT → Send Token → Client Stores Token → Authorized Requests

Security Components:

- Spring Security
- JWT Filter
- Password Encoder (BCrypt)

--------------------------------------------------

# 8. DATA FLOW

Example Flow:

User adds device.

Steps:

1. VueJS sends request
2. DeviceController receives request
3. DeviceService processes logic
4. DeviceRepository saves data
5. Database stores record
6. Response returned to client

--------------------------------------------------

# 9. ERROR HANDLING

Global Exception Handling:

Component:

GlobalExceptionHandler

Responsibilities:

- Catch system errors
- Return standard error responses
- Log exceptions

--------------------------------------------------

# 10. LOGGING SYSTEM

Logging Tool:

Spring Boot Logging

Logs include:

- API requests
- Errors
- Warnings
- System events

--------------------------------------------------

# 11. PERFORMANCE OPTIMIZATION

Techniques:

- Database indexing
- Pagination
- Lazy loading
- Query optimization

--------------------------------------------------

# 12. DEPLOYMENT ARCHITECTURE

Deployment Model:

Single Application Deployment.

Environment:

Docker (optional)

Deployment Steps:

1. Build backend (JAR)
2. Build frontend
3. Connect PostgreSQL
4. Run application

--------------------------------------------------

# 13. FUTURE EXTENSIONS

Possible Improvements:

- Redis caching
- Kafka messaging
- Microservices migration
- AI recommendation engine

--------------------------------------------------

# 14. NON-FUNCTIONAL REQUIREMENTS SUPPORT

This architecture supports:

✔ Scalability  
✔ Maintainability  
✔ Security  
✔ Performance  
✔ Reliability

--------------------------------------------------

# END OF ARCHITECTURE DOCUMENT