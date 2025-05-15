# 📱 Courses App  

## 📖 Description  
The Courses App is an Android application designed to manage academic courses and student data. It features three main views:  
- **Courses View**: Displays available courses  
- **Students View**: Shows students enrolled in a selected course  
- **Student Details View**: Presents detailed information about a selected student  

## 🛠️ Technical Specifications  
### Core Components  
- **Programming Language**: Kotlin  
- **Architecture**: MVVM (Model-View-ViewModel)  
- **Minimum SDK**: API 24 (Android 7.0)  

### Key Dependencies  
| Library | Purpose | Version |
|---------|---------|---------|
| Jetpack Compose | UI Framework | [Latest Stable] |
| Retrofit | REST API Client | 2.9.0 |
| Room | Local Database | 2.6.1 |

## ⚙️ Development Environment Requirements  
- **Android Studio**: Ladybug (2024.2.2) or newer  
- **Gradle JDK**: Version 23+  
- **Android SDK**: API 24+  

## 🚀 Installation Guide  
### 1. Repository Setup  
```bash
git clone https://github.com/gonzalodr/AndroidStudioExam
```

### 2. ❗ Important Post-Clone Configuration
>⚠️ Please follow these steps before building or running the project to avoid common issues.

✅ A. **Recommended Clone Directory**

Avoid using paths with non-ASCII characters (e.g. accents, ñ, symbols) or spaces.
For example, clone to:
```bash
C:\Dev\CoursesApp
```
✅ B. **Android SDK Configuration
Open Android Studio.**

- Go to File > Project Structure > SDK Location.
- Ensure that:
- Android SDK path is correctly set.
- JDK is set to version 23 or higher.
- Gradle JDK matches the required version (JDK 23+).

✅ C. **Configure Local IP for API Access**

Find your LAN IP address (not 127.0.0.1 or localhost):

- On Windows: ipconfig
- On Linux/macOS: ifconfig or ip a
- Look for something like 192.168.x.x
- Edit the file:
```bash
app/src/main/res/xml/network_security_config.xml
```
- Replace the placeholder or default IP with your LAN IP:

```Xml
<domain includeSubdomains="true">192.168.x.x</domain>
```
- Edit the Constant base URL in common/Constant class (e.g. Constants.kt):
```bash
private const val DEV_IP = "192.168.x.x"
```
- Make sure:

    - Your ASP.NET API is running and configured to accept remote connections.

    - Your firewall allows inbound traffic on the API port (e.g. 5275).

    - Your Android device and development machine are on the same local network.

✅ D. Build & Run
    - Click Sync Project with Gradle Files.

Wait for dependencies to resolve.

Run the app on an emulator or real device connected to the same network as your backend API.

