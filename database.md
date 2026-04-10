# DATABASE DESIGN — SMART HOME SYSTEM (FINAL VERSION)

Version: 1.0  
Status: Production-Ready  
Database: PostgreSQL (Recommended)

--------------------------------------------------

# 1. SYSTEM OVERVIEW

This database supports a Smart Home Advisory Web System.

Main Features:

- User registration & login
- Home management
- Room management
- Device tracking
- Energy usage tracking
- Smart recommendation engine
- Energy-saving tips
- Reports and analytics
- Notifications

Architecture:

Spring Boot + VueJS + Relational Database

--------------------------------------------------

# 2. CORE DESIGN PRINCIPLES

This design follows:

✔ Normalized Schema (3NF)  
✔ Many-to-Many Relationships  
✔ Audit Fields Support  
✔ Scalable Energy Logging  
✔ Optimized Indexing  
✔ Future Extension Ready

--------------------------------------------------

# 3. TABLE DEFINITIONS

==================================================
TABLE: roles
==================================================

Description:
Store system roles.

Columns:

- id (PK, BIGINT)
- role_name (VARCHAR, UNIQUE)

Example:

ADMIN  
USER

--------------------------------------------------

==================================================
TABLE: users
==================================================

Description:
Store user accounts.

Columns:

- id (PK, BIGINT)
- full_name (VARCHAR)
- email (VARCHAR, UNIQUE)
- password (VARCHAR)
- phone (VARCHAR)
- role_id (FK → roles.id)

Audit Fields:

- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

Indexes:

- UNIQUE(email)
- INDEX(role_id)

--------------------------------------------------

==================================================
TABLE: user_profiles
==================================================

Description:
Detailed user information.

Columns:

- id (PK, BIGINT)
- user_id (FK → users.id)

Profile Info:

- address (VARCHAR)
- city (VARCHAR)
- country (VARCHAR)

Audit Fields:

- created_at
- updated_at

Indexes:

- INDEX(user_id)

--------------------------------------------------

==================================================
TABLE: homes
==================================================

Description:
Store homes belonging to users.

Columns:

- id (PK, BIGINT)
- user_id (FK → users.id)

Home Info:

- home_name (VARCHAR)
- home_type (VARCHAR)

Physical Info:

- area (DOUBLE)
- number_of_rooms (INT)
- number_of_floors (INT)
- build_year (INT)

Audit Fields:

- created_at
- updated_at

Indexes:

- INDEX(user_id)

--------------------------------------------------

==================================================
TABLE: rooms
==================================================

Description:
Rooms inside homes.

Columns:

- id (PK, BIGINT)
- home_id (FK → homes.id)

Room Info:

- room_name (VARCHAR)
- room_type (VARCHAR)
- area (DOUBLE)

Audit Fields:

- created_at
- updated_at

Indexes:

- INDEX(home_id)

--------------------------------------------------

==================================================
TABLE: device_types
==================================================

Description:
Device categories.

Columns:

- id (PK, BIGINT)
- type_name (VARCHAR)
- description (TEXT)

Audit Fields:

- created_at
- updated_at

Indexes:

- UNIQUE(type_name)

Example:

Air Conditioner  
Lighting  
Water Heater

--------------------------------------------------

==================================================
TABLE: devices
==================================================

Description:
Physical devices inside rooms.

Columns:

- id (PK, BIGINT)
- room_id (FK → rooms.id)
- device_type_id (FK → device_types.id)

Device Info:

- device_name (VARCHAR)

Device Identity:

- brand (VARCHAR)
- model_number (VARCHAR)

Technical Info:

- power_rating (DOUBLE)

Audit Fields:

- created_at
- updated_at

Indexes:

- INDEX(room_id)
- INDEX(device_type_id)

Notes:

brand + model_number improves accuracy of recommendations.

--------------------------------------------------

==================================================
TABLE: device_usage_logs
==================================================

Description:
Daily usage tracking.

Columns:

- id (PK, BIGINT)
- device_id (FK → devices.id)

Usage Info:

- usage_date (DATE)
- hours_used (DOUBLE)

Audit Fields:

- created_at

Indexes:

- INDEX(device_id)
- INDEX(usage_date)
- INDEX(device_id, usage_date)

--------------------------------------------------

==================================================
TABLE: energy_records
==================================================

Description:
Energy consumption tracking.

Columns:

- id (PK, BIGINT)
- device_id (FK → devices.id)

Energy Info:

- usage_date (DATE)
- energy_consumed (DOUBLE)

Audit Fields:

- created_at

Indexes (CRITICAL):

- INDEX(device_id)
- INDEX(usage_date)
- COMPOSITE INDEX(device_id, usage_date)

Scaling Note:

Partition by usage_date if table grows large.

--------------------------------------------------

==================================================
TABLE: electricity_bills
==================================================

Description:
Monthly electricity bills.

Columns:

- id (PK, BIGINT)
- home_id (FK → homes.id)

Billing Info:

- month (INT)
- year (INT)

Energy Info:

- total_kwh (DOUBLE)
- total_cost (DOUBLE)

Audit Fields:

- created_at
- updated_at

Indexes:

- INDEX(home_id)

--------------------------------------------------

==================================================
TABLE: tip_categories
==================================================

Description:
Tip grouping.

Columns:

- id (PK, BIGINT)
- category_name (VARCHAR)

Audit Fields:

- created_at
- updated_at

Indexes:

- UNIQUE(category_name)

--------------------------------------------------

==================================================
TABLE: tips
==================================================

Description:
Energy-saving tips.

Columns:

- id (PK, BIGINT)

Content:

- title (VARCHAR)
- description (TEXT)

Category:

- category_id (FK → tip_categories.id)

Impact:

- estimated_saving (DOUBLE)

Audit Fields:

- created_at
- updated_at

Indexes:

- INDEX(category_id)

--------------------------------------------------

==================================================
TABLE: recommendations
==================================================

Description:
Many-to-Many bridge between Homes and Tips.

Columns:

- id (PK, BIGINT)

Relations:

- home_id (FK → homes.id)
- tip_id (FK → tips.id)

Tracking:

- status (ENUM)

ENUM Values:

PENDING  
IMPLEMENTED  
IGNORED

Timing:

- recommendation_date (DATE)

Audit Fields:

- created_at
- updated_at

Indexes:

- INDEX(home_id)
- INDEX(tip_id)
- INDEX(status)

--------------------------------------------------

==================================================
TABLE: reports
==================================================

Description:
Energy analysis reports.

Columns:

- id (PK, BIGINT)
- home_id (FK → homes.id)

Energy Data:

- total_energy_before (DOUBLE)
- total_energy_after (DOUBLE)

Result:

- saving_percentage (DOUBLE)

Audit Fields:

- created_at
- updated_at

Indexes:

- INDEX(home_id)

--------------------------------------------------

==================================================
TABLE: notifications
==================================================

Description:
User alerts.

Columns:

- id (PK, BIGINT)
- user_id (FK → users.id)

Content:

- message (TEXT)

Status:

- is_read (BOOLEAN)

Audit Fields:

- created_at
- updated_at

Indexes:

- INDEX(user_id)

--------------------------------------------------

# 4. RELATIONSHIP SUMMARY

roles → users  
users → homes  
homes → rooms  
rooms → devices  
devices → device_usage_logs  
devices → energy_records  
homes → electricity_bills  
tips → recommendations  
homes → recommendations  
homes → reports  
users → notifications

--------------------------------------------------

# 5. INDEX STRATEGY (PERFORMANCE)

Critical Indexes:

users:

- UNIQUE(email)

devices:

- INDEX(room_id)

energy_records:

- INDEX(device_id)
- INDEX(usage_date)
- INDEX(device_id, usage_date)

recommendations:

- INDEX(home_id)
- INDEX(tip_id)

--------------------------------------------------

# 6. ENUM DEFINITIONS

RecommendationStatus:

PENDING  
IMPLEMENTED  
IGNORED

--------------------------------------------------

# 7. SCALING STRATEGY

Large Data Tables:

- energy_records
- device_usage_logs

Solutions:

- Partition by date
- Archive historical data
- Use batch inserts

--------------------------------------------------

# 8. FUTURE EXTENSIONS

Optional Tables:

- solar_systems
- water_usage_records
- ai_recommendation_logs
- smart_sensor_data
- maintenance_logs

--------------------------------------------------

# END OF DATABASE DESIGN