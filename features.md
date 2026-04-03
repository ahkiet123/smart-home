# FEATURES DEFINITION — SMART HOME SYSTEM

Version: 2.0  
Project Type: Smart Home Advisory Web System  
Architecture: Monolithic

--------------------------------------------------

# 1. OVERVIEW

This document defines all functional features required for the Smart Home Advisory System.

Each feature includes:

- Feature Name
- Description
- Actors
- Trigger
- Main Flow
- Alternate Flow
- Validation Rules
- Edge Cases
- Failure Scenarios

--------------------------------------------------

# FEATURE 1 — USER REGISTRATION

Description:

Allows new users to create an account.

Actors:

Guest User

Trigger:

User clicks "Register".

Main Flow:

1. User enters name
2. User enters email
3. User enters password
4. User submits form
5. System validates data
6. System saves user
7. System returns success

Alternate Flow:

Email already exists.

Validation Rules:

- Email must be unique
- Password required
- Name required

Edge Cases:

Empty form.

Failure Scenarios:

Database failure.

--------------------------------------------------

# FEATURE 2 — USER LOGIN

Description:

Allows users to login.

Actors:

Registered User

Trigger:

User clicks "Login".

Main Flow:

1. User enters email
2. User enters password
3. System validates credentials
4. System generates JWT
5. User logged in

Alternate Flow:

Wrong password.

Validation Rules:

Email format valid.

Edge Cases:

Multiple failed login attempts.

Failure Scenarios:

Authentication failure.

--------------------------------------------------

# FEATURE 3 — USER PROFILE MANAGEMENT

Description:

Manage profile information.

Actors:

User

Trigger:

User opens profile page.

Main Flow:

1. View profile
2. Update profile
3. Save changes

Validation Rules:

Email format valid.

Failure Scenarios:

Update failure.

--------------------------------------------------

# FEATURE 4 — HOME MANAGEMENT

Description:

Manage homes.

Actors:

User

Trigger:

User clicks "Add Home".

Main Flow:

1. Add home
2. Update home
3. Delete home
4. View homes

Validation Rules:

- Area > 0
- Home name required

Failure Scenarios:

Database error.

--------------------------------------------------

# FEATURE 5 — ROOM MANAGEMENT

Description:

Manage rooms inside homes.

Actors:

User

Trigger:

User clicks "Add Room".

Main Flow:

1. Add room
2. Update room
3. Delete room
4. View rooms

Validation Rules:

Room area > 0.

Failure Scenarios:

Save failure.

--------------------------------------------------

# FEATURE 6 — DEVICE MANAGEMENT

Description:

Manage devices.

Actors:

User

Trigger:

User clicks "Add Device".

Main Flow:

1. Add device
2. Update device
3. Delete device
4. View devices

Validation Rules:

- Power rating > 0
- Brand required
- Model number required

Failure Scenarios:

Insert failure.

--------------------------------------------------

# FEATURE 7 — DEVICE USAGE TRACKING

Description:

Track daily device usage.

Actors:

User

Trigger:

User inputs usage hours.

Main Flow:

1. Select device
2. Enter usage hours
3. Select date
4. Save usage

Validation Rules:

- hours_used >= 0
- hours_used <= 24
- usage_date must not be future

Edge Cases:

25 hours entered.

Failure Scenarios:

Save failure.

--------------------------------------------------

# FEATURE 8 — ENERGY RECORD GENERATION

Description:

Calculate energy consumption.

Actors:

System

Trigger:

Usage record saved.

Main Flow:

1. Read device power
2. Calculate energy
3. Save energy record

Validation Rules:

Energy >= 0.

Failure Scenarios:

Calculation error.

--------------------------------------------------

# FEATURE 9 — TIP MANAGEMENT (ADMIN)

Description:

Manage energy-saving tips.

Actors:

Admin

Trigger:

Admin accesses tip page.

Main Flow:

1. Add tip
2. Update tip
3. Delete tip
4. View tips

Validation Rules:

Title required.

Failure Scenarios:

Save failure.

--------------------------------------------------

# FEATURE 10 — TIP VIEWING

Description:

Display tips to users.

Actors:

User

Trigger:

User opens tips page.

Main Flow:

1. Load tips
2. Display tips

Failure Scenarios:

Load failure.

--------------------------------------------------

# FEATURE 11 — RECOMMENDATION GENERATION (CORE)

Description:

Generate smart recommendations.

Actors:

User  
System Scheduler

Trigger:

Manual:

User clicks "Generate Recommendations"

Automatic:

Weekly scheduled job.

Main Flow (Manual):

1. User clicks generate
2. Load home data
3. Load device usage
4. Analyze data
5. Match tips
6. Save recommendations
7. Display results

Main Flow (Scheduled):

1. Weekly scheduler runs
2. Scan all homes
3. Analyze data
4. Generate recommendations
5. Save results

Validation Rules:

Home must exist.

Edge Cases:

No devices.

Failure Scenarios:

Processing failure.

--------------------------------------------------

# FEATURE 12 — RECOMMENDATION STATUS UPDATE

Description:

Update recommendation status.

Actors:

User

Trigger:

User updates status.

Status Values:

- PENDING
- IMPLEMENTED
- IGNORED

Validation Rules:

Valid enum only.

Failure Scenarios:

Update failure.

--------------------------------------------------

# FEATURE 13 — ELECTRICITY BILL MANAGEMENT

Description:

Allow users to input monthly electricity bills.

Actors:

User

Trigger:

User adds electricity bill.

Main Flow:

1. Select home
2. Enter month
3. Enter year
4. Enter kWh
5. Enter cost
6. Save bill

Validation Rules:

- month between 1–12
- Only one bill per month
- total_kwh >= 0
- total_cost >= 0

Failure Scenarios:

Duplicate bill.

--------------------------------------------------

# FEATURE 14 — ENERGY COMPARISON

Description:

Compare calculated energy vs actual bill.

Actors:

System

Trigger:

Electricity bill saved.

Main Flow:

1. Load energy records
2. Load bill
3. Compare values
4. Calculate difference
5. Store comparison

Failure Scenarios:

Comparison error.

--------------------------------------------------

# FEATURE 15 — REPORT GENERATION

Description:

Generate energy reports.

Actors:

User

Trigger:

User requests report.

Main Flow:

1. Load data
2. Calculate savings
3. Generate report
4. Display report

Failure Scenarios:

Calculation failure.

--------------------------------------------------

# FEATURE 16 — DASHBOARD VIEW

Description:

Display overview dashboard.

Actors:

User

Trigger:

User opens dashboard.

Main Flow:

1. Load summary
2. Display charts

Failure Scenarios:

Load failure.

--------------------------------------------------

# FEATURE 17 — NOTIFICATION MANAGEMENT

Description:

Manage system notifications.

Actors:

User

Trigger:

System sends notification.

Main Flow:

1. Receive notification
2. View notification
3. Mark as read

Failure Scenarios:

Load failure.

--------------------------------------------------

# FEATURE 18 — ROLE MANAGEMENT

Description:

Manage roles.

Actors:

Admin

Trigger:

Admin accesses role page.

Main Flow:

1. Create role
2. Assign role
3. Update role

Failure Scenarios:

Save failure.

--------------------------------------------------

# FEATURE 19 — AUTHORIZATION CONTROL

Description:

Control system access.

Actors:

System

Trigger:

User sends request.

Main Flow:

1. Validate JWT
2. Check role
3. Allow access

Failure Scenarios:

Access denied.

--------------------------------------------------

# FEATURE SUMMARY

Total Features:

19 Features

Core Features:

Authentication  
Home Management  
Device Management  
Energy Tracking  
Recommendation Engine  
Electricity Bill  
Reports

--------------------------------------------------

# END OF FEATURES DOCUMENT