# 🅿️ ParkSpot – Never Forget Where You Parked

An intelligent Android SaaS app that automatically saves your parking location and helps you navigate back with ease.

## 🚀 Features

### Core Features (MVP)
- ✅ **Automatic Parking Detection** - Saves location when you disconnect from car Bluetooth
- ✅ **GPS Location Capture** - High-accuracy location with address geocoding
- ✅ **Arrow Navigation** - Real-time compass-based navigation back to your car
- ✅ **Photo Capture** - Take photos of your parking spot
- ✅ **Notes** - Add text notes to parking sessions
- ✅ **Parking History** - View past parking sessions (7 days free, unlimited premium)
- ✅ **Multi-Car Support** - Manage multiple vehicles (1 free, unlimited premium)
- ✅ **Offline Mode** - Core features work without internet

### Premium Features ($4.99/month or $39.99/year)
- 🌟 Unlimited vehicles
- 🌟 Up to 5 photos per session
- 🌟 Unlimited history with CSV export
- 🌟 Parking timer with notifications
- 🌟 AR navigation mode
- 🌟 Cloud backup & sync across devices
- 🌟 Family sharing (up to 5 members)

## 📱 Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: Clean Architecture + MVVM
- **Dependency Injection**: Hilt
- **Database**: Room (SQLite)
- **Async**: Kotlin Coroutines + Flow
- **Location**: Google Play Services Fused Location API
- **Sensors**: Bluetooth, GPS, Compass (Accelerometer + Magnetometer)
- **Backend**: Firebase (Auth, Firestore, Storage, Analytics)
- **Billing**: Google Play Billing
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)

## 🏗️ Architecture

The app follows Clean Architecture with three layers:

```
Presentation Layer (UI)
    ↓
Domain Layer (Business Logic)
    ↓
Data Layer (Database + Sensors)
```

### Package Structure
```
com.parkspot.app/
├── presentation/ (Compose UI + ViewModels)
├── domain/ (Models, Use Cases, Repository Interfaces)
├── data/ (Repository Implementations, DAOs, Sensors)
├── di/ (Hilt Dependency Injection)
└── util/ (Constants, Extensions)
```

## 🔧 Setup Instructions

### Prerequisites
- Android Studio Hedgehog or newer
- Android SDK 34
- Kotlin 1.9.20+
- Java 17

### Clone and Build
```bash
git clone https://github.com/SyedTahaAlam/ParkSpot-Android.git
cd ParkSpot-Android
./gradlew build
```

### Firebase Setup (Optional for Cloud Features)
1. Create Firebase project at https://console.firebase.google.com/
2. Add Android app with package name: `com.parkspot.app`
3. Download `google-services.json`
4. Place in `app/` directory

### Run the App
1. Open project in Android Studio
2. Sync Gradle
3. Connect device or start emulator (API 26+)
4. Click Run ▶️

## 📋 Permissions Required

- **Location** (Fine, Coarse, Background) - GPS tracking and auto-detection
- **Bluetooth** - Car disconnect detection
- **Camera** - Parking spot photos
- **Notifications** - Parking timer alerts

## 🎨 UI Screenshots

(Add screenshots here when available)

## 🧪 Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## 📦 Dependencies

Major libraries used:
- Jetpack Compose BOM 2023.10.01
- Hilt 2.48
- Room 2.6.1
- Navigation Compose 2.7.5
- Google Play Services Location 21.0.1
- Firebase BOM 32.7.0
- Google Play Billing 6.1.0
- Coil Compose 2.5.0

## 🚀 Roadmap

### Phase 1 (Current - MVP)
- [x] Manual parking save
- [x] Arrow navigation
- [x] Basic history
- [ ] Permission flows
- [ ] Photo capture
- [ ] Bluetooth auto-detection

### Phase 2 (v1.1)
- [ ] Parking timer & notifications
- [ ] Firebase cloud sync
- [ ] Google Play Billing integration
- [ ] Multi-vehicle management

### Phase 3 (v2.0)
- [ ] AR navigation mode
- [ ] Family sharing
- [ ] Parking analytics
- [ ] Widget support

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

Copyright © 2026 SyedTahaAlam. All rights reserved.

## 👤 Author

**Syed Taha Alam**
- GitHub: [@SyedTahaAlam](https://github.com/SyedTahaAlam)

## 🙏 Acknowledgments

- Material Design 3 guidelines
- Android Architecture Components
- Clean Architecture by Robert C. Martin

---

**Built with ❤️ using Jetpack Compose**