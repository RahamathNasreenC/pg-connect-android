# PG Connect – Android Application

## Overview
PG Connect is an Android mobile application designed to help users discover and request Paying Guest (PG) accommodations. The app provides a simple platform where users can browse available PG properties, view details, and send booking requests. Administrators can manage property listings and handle booking requests through a dedicated admin interface.

This project aims to simplify the process of finding and managing PG accommodations through a mobile application.

---

## Features

### User Features
- User registration and login
- Browse available PG properties
- View detailed property information
- Send booking requests
- Track booking request status

### Admin Features
- Admin dashboard
- Add new PG property listings
- Delete or manage existing properties
- View booking requests from users
- Accept or reject booking requests

---

## Tech Stack

- **Platform:** Android Mobile Application  
- **IDE:** Android Studio  
- **Programming Language:** Java  
- **Database:** SQLite (via DatabaseHelper)  
- **UI Design:** XML Layouts  

---

## Application Modules

### Authentication
Handles user login and registration.

Activities:
- LoginActivity
- RegisterActivity

### Property Browsing
Allows users to explore available PG properties and view details.

Activities:
- UserDashboardActivity
- PropertyDetailActivity
- PropertyAdapter

### Booking System
Handles booking requests and booking status tracking.

Activities:
- UserBookingActivity
- BookingTrackerActivity
- ConfirmationActivity

### Admin Management
Allows administrators to manage property listings and booking requests.

Activities:
- AdminDashboardActivity
- AdminBookingRequestsActivity
- AddPropertyActivity
- DeletePropertyActivity

### Notifications
Displays booking updates and confirmations to users.

Activities:
- NotificationActivity

---

## Project Structure

```
PGConnect
│
├── app
│   └── src/main
│       ├── java/com/example/pgconnect
│       ├── res/layout
│       ├── res/drawable
│       └── AndroidManifest.xml
│
├── build.gradle
├── settings.gradle
└── .gitignore
```

---

## How to Run the Project

1. Clone the repository

```
git clone https://github.com/your-username/pg-connect-android.git
```

2. Open the project in **Android Studio**

3. Allow Gradle to sync dependencies

4. Run the project using an **Android Emulator** or a **physical device**

---

## Future Improvements

- Location-based PG search
- Google Maps integration
- Online booking confirmation
- Payment gateway integration
- User reviews and ratings
- Push notifications

---


**Rahamath Nasreen**
