# API CONTRACT — SMART HOME SYSTEM

## MODULE 1 — AUTHENTICATION

### Register User
Method: POST
URL: /api/auth/register
Description: Creates a new user account with a default 'USER' role.

**Request Headers**
Content-Type: application/json

**Request Body (JSON)**
```json
{
  "full_name": "John Doe",
  "email": "john.doe@example.com",
  "password": "StrongPassword123!",
  "phone": "+1234567890"
}
```

**Response Body (SUCCESS)**
```json
{
  "timestamp": "2026-03-28T10:00:00.000Z",
  "status": "SUCCESS",
  "message": "Registration successful",
  "data": { "id": 1, "email": "john.doe@example.com" }
}
```

**Validation Rules**
* `email`: Must be unique and valid format.
* `password`: Minimum 8 characters.
* `full_name`: Required.

**Authorization Rules**
Public endpoint.

**Business Rules**
* Passwords must be hashed using BCrypt.
* Automatically assigns 'USER' role.

**Database Tables Used**
`users`, `roles`

---

### Login
Method: POST
URL: /api/auth/login
Description: Authenticates credentials and returns a JWT.

**Request Body (JSON)**
```json
{
  "email": "john.doe@example.com",
  "password": "StrongPassword123!"
}
```

**Response Body (SUCCESS)**
```json
{
  "timestamp": "2026-03-28T10:05:00.000Z",
  "status": "SUCCESS",
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbG...",
    "refreshToken": "eyJhbG...",
    "user": { "id": 1, "role": "USER" }
  }
}
```

---

### Refresh Token
Method: POST
URL: /api/auth/refresh
Description: Rotates the access token.

**Request Body (JSON)**
```json
{ "refreshToken": "eyJhbG..." }
```

---

### Logout
Method: POST
URL: /api/auth/logout
Description: Invalidates the current JWT session.

---

## MODULE 2 — USER

### Get My Profile
Method: GET
URL: /api/users/me
Description: Retrieves authenticated user details.

**Response Body (SUCCESS)**
```json
{
  "timestamp": "2026-03-28T10:10:00.000Z",
  "status": "SUCCESS",
  "message": "Profile retrieved",
  "data": {
    "id": 1,
    "full_name": "John Doe",
    "profile": { "city": "Tech City", "country": "Innovaland" }
  }
}
```

**Database Tables Used**
`users`, `user_profiles`

---

### Update Profile
Method: PUT
URL: /api/users/profile
Description: Updates profile info.

---

## MODULE 3 — HOME

### List My Homes
Method: GET
URL: /api/homes
Description: Returns paginated homes for the user.

**Request Parameters**
Query Params: `page=0`, `size=10`

**Response Body (SUCCESS)**
```json
{
  "timestamp": "2026-03-28T10:15:00.000Z",
  "status": "SUCCESS",
  "message": "Homes retrieved",
  "data": {
    "content": [{ "id": 10, "home_name": "Smart Villa", "area": 150.0 }],
    "totalElements": 1
  }
}
```

---

### Add Home
Method: POST
URL: /api/homes
Description: Adds a new home.

**Validation Rules**
* `area` > 0.
* `home_name`: Required.

---

## MODULE 4 — ROOM

### List Rooms in Home
Method: GET
URL: /api/rooms?homeId=10
Description: Retrieves rooms for a home.

**Request Parameters**
Query Params: `homeId` (Required), `page=0`, `size=10`

---

### Add Room
Method: POST
URL: /api/rooms
Description: Adds a room to a home.

**Business Rules**
* Validates user ownership of the home.

---

## MODULE 5 — DEVICE

### List Devices
Method: GET
URL: /api/devices
Description: List devices with optional room filtering.

**Request Parameters**
Query Params: `roomId` (optional), `page=0`, `size=10`

---

### Add Device
Method: POST
URL: /api/devices
Description: Registers a new device.

**Validation Rules**
* `power_rating` > 0.
* `brand`, `model_number`: Required.

---

## MODULE 6 — ENERGY

### Log Device Usage
Method: POST
URL: /api/usage-logs
Description: Records daily usage hours.

**Request Body (JSON)**
```json
{
  "device_id": 500,
  "usage_date": "2026-03-27",
  "hours_used": 5.5
}
```

**Business Rules**
* **Energy Record Generation:** System calculates `energy_consumed = power_rating × hours_used` and saves to `energy_records`.

**Database Tables Used**
`device_usage_logs`, `energy_records`, `devices`

---

### Add Electricity Bill
Method: POST
URL: /api/energy/bills
Description: Log actual monthly bill.

**Validation Rules**
* `month` between 1-12.
* `total_kwh` >= 0.

---

## MODULE 7 — TIP

### List Tips
Method: GET
URL: /api/tips
Description: Retrieves saving tips.

**Request Parameters**
Query Params: `categoryId` (optional), `page=0`, `size=10`

---

### Create Tip (ADMIN ONLY)
Method: POST
URL: /api/tips
Description: Admin adds a new tip.

---

## MODULE 8 — RECOMMENDATION

### Generate Recommendations
Method: POST
URL: /api/recommendations/generate
Description: Triggers pattern matching for home efficiency.

**Request Body (JSON)**
```json
{ "home_id": 10 }
```

**Business Rules**
* Matches `tips` to `home` based on `device_types` and usage history.

**Database Tables Used**
`recommendations`, `tips`, `devices`

---

### Update Status
Method: PUT
URL: /api/recommendations/{id}/status
Description: Updates status to IMPLEMENTED/IGNORED.

**Validation Rules**
* Status must be `PENDING`, `IMPLEMENTED`, or `IGNORED`.

---

## MODULE 9 — REPORT

### Get Energy Report
Method: GET
URL: /api/reports?homeId=10
Description: Calculates saving percentages.

**Business Rules**
* `saving_percentage` = `(energy_before - energy_after) / energy_before`.

---

## MODULE 10 — NOTIFICATION

### List My Notifications
Method: GET
URL: /api/notifications
Description: User alerts.

**Response Body (SUCCESS)**
```json
{
  "timestamp": "2026-03-28T10:55:00.000Z",
  "status": "SUCCESS",
  "message": "Notifications retrieved",
  "data": [{ "id": 101, "message": "High AC usage!", "is_read": false }]
}
```

---

### Mark as Read
Method: PUT
URL: /api/notifications/{id}/read
Description: Mark notification as read.

---

## MODULE 11 — ROLE

### List Roles (ADMIN ONLY)
Method: GET
URL: /api/roles
Description: Retrieves system roles.

---

### Assign Role (ADMIN ONLY)
Method: PUT
URL: /api/roles/assign
Description: Admin changes user role.

**Request Body (JSON)**
```json
{ "user_id": 1, "role_id": 1 }
```

**Database Tables Used**
`roles`, `users`

# END OF API CONTRACT