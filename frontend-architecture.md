# FRONTEND ARCHITECTURE — SMART HOME SYSTEM

Version: 1.0  
Frontend Framework: VueJS  
State Management: Pinia  
Routing: Vue Router  
HTTP Client: Axios  
UI Style: Component-Based Architecture

--------------------------------------------------

# 1. FRONTEND OVERVIEW

The frontend of the Smart Home Advisory System is developed using VueJS.

Architecture Type:

Component-Based Architecture with Modular Structure.

Main Responsibilities:

- Display UI
- Collect user input
- Send API requests
- Render data
- Manage state
- Handle navigation

--------------------------------------------------

# 2. FRONTEND ARCHITECTURE STYLE

Frontend follows:

✔ Component-Based Design  
✔ Modular Folder Structure  
✔ Separation of Concerns  
✔ Reusable Components

Main Layers:

1. Views
2. Components
3. Services
4. Store
5. Router
6. Utilities

--------------------------------------------------

# 3. FRONTEND FOLDER STRUCTURE

Root:

src/

Structure:

src/

├── assets/  
│       images/  
│       icons/  
│       styles/

├── components/  
│       common/  
│               Navbar.vue  
│               Sidebar.vue  
│               Footer.vue  
│               LoadingSpinner.vue  
│               ConfirmDialog.vue

│       home/  
│               HomeCard.vue  
│               HomeForm.vue

│       room/  
│               RoomCard.vue  
│               RoomForm.vue

│       device/  
│               DeviceCard.vue  
│               DeviceForm.vue

│       tip/  
│               TipCard.vue

│       recommendation/  
│               RecommendationCard.vue

│       report/  
│               ReportChart.vue

├── views/  
│       LoginView.vue  
│       RegisterView.vue  
│       DashboardView.vue  
│       HomeView.vue  
│       RoomView.vue  
│       DeviceView.vue  
│       TipView.vue  
│       RecommendationView.vue  
│       ReportView.vue  
│       NotificationView.vue

├── router/  
│       index.js

├── store/  
│       authStore.js  
│       userStore.js  
│       homeStore.js  
│       deviceStore.js  
│       recommendationStore.js  
│       reportStore.js

├── services/  
│       api.js  
│       authService.js  
│       userService.js  
│       homeService.js  
│       deviceService.js  
│       recommendationService.js  
│       reportService.js

├── utils/  
│       dateUtil.js  
│       energyUtil.js

├── constants/  
│       apiEndpoints.js

├── layouts/  
│       MainLayout.vue  
│       AuthLayout.vue

├── App.vue

└── main.js

--------------------------------------------------

# 4. VIEWS LAYER

Views represent full pages.

Responsibilities:

- Display page layout
- Load data
- Call services
- Render components

Main Views:

LoginView:

Purpose:

User login.

Components:

LoginForm

--------------------------------------------------

RegisterView:

Purpose:

User registration.

--------------------------------------------------

DashboardView:

Purpose:

Display overview data.

Shows:

- Total homes
- Devices summary
- Energy usage

--------------------------------------------------

HomeView:

Purpose:

Manage homes.

Features:

- Create home
- Update home
- Delete home

--------------------------------------------------

RoomView:

Purpose:

Manage rooms.

--------------------------------------------------

DeviceView:

Purpose:

Manage devices.

--------------------------------------------------

TipView:

Purpose:

Display energy-saving tips.

--------------------------------------------------

RecommendationView:

Purpose:

Display recommendations.

Features:

- View tips
- Update status

--------------------------------------------------

ReportView:

Purpose:

Display reports.

Includes:

Charts  
Statistics

--------------------------------------------------

NotificationView:

Purpose:

Display system notifications.

--------------------------------------------------

# 5. COMPONENT LAYER

Reusable UI blocks.

Responsibilities:

- Render UI elements
- Accept props
- Emit events

Common Components:

Navbar.vue  
Sidebar.vue  
Footer.vue

Reusable Components:

HomeCard.vue  
DeviceCard.vue  
RecommendationCard.vue

Form Components:

HomeForm.vue  
DeviceForm.vue

Chart Components:

ReportChart.vue

--------------------------------------------------

# 6. SERVICE LAYER (API CALLS)

Responsible for:

- Calling backend APIs
- Handling HTTP requests
- Returning data

Technology:

Axios

Base File:

api.js

Example:

axios.create({
baseURL: "/api"
})

Service Files:

authService.js  
homeService.js  
deviceService.js  
recommendationService.js

Example Methods:

login()

getHomes()

createDevice()

getRecommendations()

--------------------------------------------------

# 7. STATE MANAGEMENT (STORE)

Technology:

Pinia

Responsibilities:

- Store global data
- Share data across components
- Manage authentication state

Stores:

authStore:

Stores:

- token
- user info

homeStore:

Stores:

- homes list

deviceStore:

Stores:

- devices list

recommendationStore:

Stores:

- recommendations

reportStore:

Stores:

- reports

--------------------------------------------------

# 8. ROUTING SYSTEM

Technology:

Vue Router

File:

router/index.js

Responsibilities:

- Navigation control
- Route mapping
- Protected routes

Example Routes:

/login  
/register  
/dashboard  
/homes  
/devices  
/reports

Protected Routes:

Require login.

--------------------------------------------------

# 9. LAYOUT SYSTEM

Layouts define page structure.

Main Layout:

MainLayout.vue

Includes:

Navbar  
Sidebar  
Footer

Auth Layout:

AuthLayout.vue

Used for:

Login  
Register

--------------------------------------------------

# 10. AUTHENTICATION FLOW

Login Flow:

User enters credentials →  
Send login request →  
Receive JWT →  
Store token →  
Access protected routes.

Token Storage:

LocalStorage.

Authorization Header:

Authorization: Bearer JWT

--------------------------------------------------

# 11. DATA FLOW

Example:

User adds device.

Steps:

1. User submits form
2. Component emits event
3. Service sends API request
4. Store updates state
5. UI updates

--------------------------------------------------

# 12. ERROR HANDLING

Handled in:

api.js

Handles:

- API errors
- Network errors
- Unauthorized errors

Displays:

Error messages to users.

--------------------------------------------------

# 13. FORM VALIDATION

Validation Tools:

Vue built-in validation  
or VeeValidate (optional)

Validation Examples:

Required fields  
Email format  
Number range

--------------------------------------------------

# 14. PERFORMANCE OPTIMIZATION

Techniques:

- Lazy loading routes
- Component reuse
- Pagination
- Caching data

Example:

const Dashboard = () => import("./DashboardView.vue")

--------------------------------------------------

# 15. CHART & DASHBOARD SUPPORT

Charts used in:

ReportView

Recommended Library:

Chart.js  
or ECharts

Displays:

- Energy usage trends
- Saving statistics

--------------------------------------------------

# 16. UI DESIGN PRINCIPLES

Design Style:

Dashboard-based layout.

Focus:

✔ Clean UI  
✔ Responsive design  
✔ Easy navigation

--------------------------------------------------

# 17. RESPONSIVE DESIGN

Supported Devices:

Desktop  
Tablet  
Mobile

Recommended Tool:

CSS Flexbox  
or TailwindCSS

--------------------------------------------------

# 18. FUTURE FRONTEND EXTENSIONS

Possible Improvements:

- Dark mode
- Real-time updates
- Notification badges
- Multi-language support

--------------------------------------------------

# 19. FRONTEND BUILD PROCESS

Build Tool:

Vite (recommended)

Commands:

Install dependencies:

npm install

Run project:

npm run dev

Build production:

npm run build

--------------------------------------------------

# 20. NON-FUNCTIONAL REQUIREMENTS

Supported:

✔ Maintainability  
✔ Scalability  
✔ Performance  
✔ Usability

--------------------------------------------------

# END OF FRONTEND ARCHITECTURE