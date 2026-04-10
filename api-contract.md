# API CONTRACT — SMART HOME ADVISORY SYSTEM

Version: 3.0  
Architecture: RESTful API  
Authentication: JWT  
Base URL: /api/v1

--------------------------------------------------

# 1. STANDARD RESPONSE FORMAT

Success Response:

HTTP Status: 200 / 201

{
"timestamp": "YYYY-MM-DDTHH:mm:ss",
"status": "SUCCESS",
"message": "Operation successful",
"data": {}
}

--------------------------------------------------

Error Response:

HTTP Status: 400 / 401 / 403 / 404 / 500

{
"timestamp": "YYYY-MM-DDTHH:mm:ss",
"status": "ERROR",
"message": "Error message",
"errors": []
}

--------------------------------------------------

# 2. STANDARD HTTP STATUS CODES

200 OK — Successful GET / PUT / DELETE  
201 Created — Successful POST  
400 Bad Request — Validation failure  
401 Unauthorized — Invalid JWT  
403 Forbidden — Permission denied  
404 Not Found — Resource not found  
500 Internal Server Error — Server error

--------------------------------------------------

# 3. AUTH MODULE

--------------------------------------------------

## POST /auth/register

Description:

Register new user.

HTTP Status:

201 Created

Request:

{
"fullName": "John Doe",
"email": "john@example.com",
"password": "123456"
}

Validation:

- Email must be unique
- Password required
- Email format valid

--------------------------------------------------

## POST /auth/login

Description:

Login user.

HTTP Status:

200 OK

Request:

{
"email": "john@example.com",
"password": "123456"
}

Response:

{
"token": "JWT_TOKEN"
}

--------------------------------------------------

# 4. USER MODULE

--------------------------------------------------

## GET /users/profile

Description:

Get current user profile.

Authorization:

Required

HTTP Status:

200 OK

--------------------------------------------------

## PUT /users/profile

Description:

Update profile.

HTTP Status:

200 OK

Request:

{
"fullName": "John Doe",
"phone": "0123456789"
}

--------------------------------------------------

# 5. HOME MODULE

--------------------------------------------------

## POST /homes

Create home.

HTTP Status:

201 Created

Request:

{
"homeName": "My Home",
"homeType": "Apartment",
"area": 120,
"numberOfRooms": 3,
"numberOfFloors": 2,
"buildYear": 2020
}

Validation:

area > 0

--------------------------------------------------

## GET /homes

Get all homes.

Supports Pagination:

?page=0  
&size=10  
&sort=id,desc

HTTP Status:

200 OK

--------------------------------------------------

## GET /homes/{id}

Get home by ID.

HTTP Status:

200 / 404

--------------------------------------------------

## PUT /homes/{id}

Update home.

HTTP Status:

200 OK

--------------------------------------------------

## DELETE /homes/{id}

Delete home.

HTTP Status:

200 OK

--------------------------------------------------

# 6. ROOM MODULE

--------------------------------------------------

## POST /rooms

Create room.

HTTP Status:

201 Created

Request:

{
"homeId": 1,
"roomName": "Living Room",
"roomType": "Living",
"area": 25
}

--------------------------------------------------

## GET /rooms/home/{homeId}

Get rooms by home.

HTTP Status:

200 OK

--------------------------------------------------

## PUT /rooms/{id}

Update room.

--------------------------------------------------

## DELETE /rooms/{id}

Delete room.

--------------------------------------------------

# 7. DEVICE MODULE

--------------------------------------------------

## POST /devices

Create device.

HTTP Status:

201 Created

Request:

{
"roomId": 1,
"deviceTypeId": 1,
"deviceName": "Air Conditioner",
"brand": "Daikin",
"modelNumber": "AC-123",
"powerRating": 1200
}

Validation:

powerRating > 0

--------------------------------------------------

## GET /devices/room/{roomId}

Get devices by room.

--------------------------------------------------

## PUT /devices/{id}

Update device.

--------------------------------------------------

## DELETE /devices/{id}

Delete device.

--------------------------------------------------

# 8. DEVICE USAGE MODULE

--------------------------------------------------

## POST /device-usage

Add usage record.

HTTP Status:

201 Created

Request:

{
"deviceId": 1,
"usageDate": "YYYY-MM-DD",
"hoursUsed": 5
}

Validation:

hoursUsed >= 0  
hoursUsed <= 24

--------------------------------------------------

## GET /device-usage/device/{deviceId}

Get usage history.

Supports Pagination.

--------------------------------------------------

# 9. ELECTRICITY BILL MODULE

--------------------------------------------------

## POST /electricity-bills

Add monthly bill.

HTTP Status:

201 Created

Request:

{
"homeId": 1,
"month": 5,
"year": 2025,
"totalKwh": 350,
"totalCost": 850000
}

Validation:

month 1–12  
unique(homeId, month, year)

--------------------------------------------------

## GET /electricity-bills/home/{homeId}

Get bills by home.

--------------------------------------------------

# 10. TIP MODULE (ADMIN)

Authorization:

ADMIN only

--------------------------------------------------

## POST /tips

Create tip.

HTTP Status:

201 Created

--------------------------------------------------

## GET /tips

Get all tips.

--------------------------------------------------

## PUT /tips/{id}

Update tip.

--------------------------------------------------

## DELETE /tips/{id}

Delete tip.

--------------------------------------------------

# 11. RECOMMENDATION MODULE

--------------------------------------------------

## POST /recommendations/generate/{homeId}

Generate recommendations.

Trigger:

Manual: User clicks button  
Automatic: Weekly scheduler

HTTP Status:

201 Created

--------------------------------------------------

## GET /recommendations/home/{homeId}

Get recommendations.

--------------------------------------------------

## PUT /recommendations/{id}/status

Update recommendation status.

Request:

{
"status": "IMPLEMENTED"
}

Allowed:

PENDING  
IMPLEMENTED  
IGNORED

--------------------------------------------------

# 12. REPORT MODULE

--------------------------------------------------

## GET /reports/home/{homeId}

Generate report.

HTTP Status:

200 OK

--------------------------------------------------

# 13. DASHBOARD MODULE

--------------------------------------------------

## GET /dashboard

Get dashboard summary.

--------------------------------------------------

# 14. NOTIFICATION MODULE

--------------------------------------------------

## GET /notifications

Get notifications.

--------------------------------------------------

## PUT /notifications/{id}/read

Mark notification as read.

--------------------------------------------------

# 15. ROLE MODULE (ADMIN)

--------------------------------------------------

## POST /roles

Create role.

--------------------------------------------------

## PUT /users/{userId}/role/{roleId}

Assign role.

--------------------------------------------------

# 16. AUTHORIZATION RULES

JWT Required:

All endpoints except:

POST /auth/register  
POST /auth/login

Admin Only:

POST /tips  
PUT /tips  
DELETE /tips  
POST /roles  
PUT /users/{userId}/role/{roleId}

--------------------------------------------------

# 17. PAGINATION STANDARD

Query Parameters:

?page=0  
&size=10  
&sort=id,desc

--------------------------------------------------

# END OF API CONTRACT