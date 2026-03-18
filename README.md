# Student Broadcast System

## Overview
The **Student Broadcast System** is an Android application designed to facilitate seamless, structured communication between college faculty and students. Instead of relying on chaotic chat groups, this application allows registered Faculty to write custom broadcast messages targeted at specific Branches (e.g., BCA, MCA) and Semesters. 

These messages are securely held in a local SQLite database until an **Admin** reviews and approves them. Upon approval, the app automatically dispatches the message directly to the targeted students' inboxes using the native **JavaMail API** running completely in the background.

---

## Key Features

### 1. Faculty Portal
- Secure Account Registration & Login tied directly to local SQLite.
- Dashboard with dynamic Spinners to select the target **Branch** and **Semester**.
- Multi-line text field to draft the broadcast message comfortably.
- "Send for Approval" mechanism to prevent unauthorized or accidental mass emails.
- Persistent auto-login ensuring you land on your dashboard immediately on subsequent app launches.

### 2. Admin Portal
- Dashboard featuring a real-time `RecyclerView` of all pending broadcast requests waiting for clearance.
- Detailed Request View showing the exact Sender, Branch, Semester, and Content.
- One-click **"Approve and Send"** functionality.
- Persistent auto-login capabilities.

### 3. Automated Email Dispatch (JavaMail API)
- Once approved by the Admin, the app silently connects to Google's SMTP servers in the background.
- It queries the SQLite database to pull all registered student emails corresponding to the designated Branch and Semester.
- Packages all emails directly into the **BCC (Blind Carbon Copy)** field to guarantee strict student privacy.
- Delivers a professionally formatted email containing the Faculty's email address, branch, semester, and the main message.

---

## Tech Stack
- **Language:** Java (Android SDK)
- **UI Architecture:** XML (Linear and Constraint Layouts)
- **Local Database:** `SQLiteOpenHelper` 
- **Networking/Mail:** JavaMail API (`com.sun.mail:android-mail:1.6.7`)

---

## Installation & Setup Guide

1. **Clone or Download** the project repository.
2. Open the project folder in **Android Studio** (Koala / Ladybug or newer recommended).
3. Allow Gradle to sync the required dependencies structure.
4. **Configure the Email Sender (CRITICAL):**
   - Open `app/src/main/java/com/example/studentbroadcastsystem/JavaMailAPI.java`.
   - Locate the `SENDER_EMAIL` variable and enter the actual Administrator Gmail address used for broadcasting.
   - Locate the `SENDER_PASSWORD` variable. You **cannot** use your normal password. You must generate a 16-character **Google App Password**:
     1. Go to your Google Account -> Security.
     2. Enable 2-Step Verification.
     3. Search for "App passwords" and generate a new one specifically for this "Android Broadcast App".
     4. Paste the 16-letter password into the `SENDER_PASSWORD` field exactly as generated (e.g., `"oaezcbnaujgsyakd"` without spaces).
5. Build and Run the app on an Emulator or Physical Device!

---

## How to Test

1. **Launch the App:** You will be greeted by the centered Homepage giving you two portals: "Admin Login" and "Faculty Login".
2. **Login as Admin:** Click "Admin Login" and use the pre-seeded master credentials:
   - Email: `Admin.admin240@gmail.com`
   - Password: `Admin@123`
3. **Login as Faculty:** Alternatively, register yourself as a new faculty member from the Faculty Login screen!
4. **Draft a Message:** As a Faculty, select a Branch (e.g., `BCA`), a Semester (e.g., `4`), type "Hello Class! Tomorrow is a holiday.", and click Send for Approval.
5. **Approve It:** Log out of the Faculty portal, log back in as Admin, click on the shiny new pending request in your list, and hit "Approve". If your App Password in the code is correct, the email will instantly go out!

---

## Database Architecture (SQLite v3)
- **`TABLE_USERS`**: `id`, `email`, `password`, `role` (Either `admin` or `faculty`)
- **`TABLE_STUDENTS`**: `id`, `email`, `branch`, `semester`
- **`TABLE_MESSAGES`**: `id`, `sender_email`, `content`, `branch`, `semester`, `status` (Either `pending` or `approved`)
