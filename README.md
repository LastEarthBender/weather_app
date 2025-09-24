# Weather App

A modern Android weather application built with Jetpack Compose, following MVVM architecture and Clean Architecture principles.

## Features

- **Splash Screen**: Beautiful animated splash screen with smooth transition to home
- **Home Screen**: Search for weather by city name with favorite city functionality
- **Weather Detail Screen**: Comprehensive weather information display
- **Favorite City**: Save and automatically load your favorite city
- **Real-time Data**: Fetches current weather data from OpenWeather API
- **Offline Storage**: Persists user preferences using DataStore

## Architecture

This app follows **Clean Architecture** principles with **MVVM** pattern:

### Layers:
- **Presentation Layer**: UI (Jetpack Compose) + ViewModels
- **Domain Layer**: Use Cases + Repository Interfaces + Domain Models
- **Data Layer**: Repository Implementations + API Services + Data Models

### Key Components:
- **Dependency Injection**: Dagger Hilt
- **Navigation**: Navigation Compose
- **Networking**: Retrofit + OkHttp
- **Local Storage**: DataStore Preferences
- **Concurrency**: Kotlin Coroutines + Flow
- **Testing**: JUnit + MockK

## Setup Instructions

### 1. Build and Run

1. Clone the repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Run the app on an emulator or physical device

## Project Structure

```
app/src/main/java/com/example/weather_app/
├── data/
│   ├── mapper/          # Data transformation
│   ├── model/           # API response models
│   ├── remote/          # API service
│   └── repository/      # Repository implementations
├── di/                  # Dependency injection modules
├── domain/
│   ├── model/           # Domain models
│   ├── repository/      # Repository interfaces
│   ├── usecase/         # Business logic use cases
│   └── util/            # Utility classes
├── presentation/
│   ├── home/            # Home screen
│   ├── navigation/      # Navigation setup
│   ├── splash/          # Splash screen
│   └── weather_detail/  # Weather detail screen
├── ui/theme/            # App theming
├── MainActivity.kt      # Main activity
└── WeatherApplication.kt # Application class
```

## Features Implementation

### 1. Splash Screen ✅
- Animated logo and text
- 3-second display duration
- Smooth navigation to home screen

### 2. Home Screen ✅
- City name input field
- Search button with loading indicator
- Favorite city functionality (heart icon)
- Weather preview card
- Error handling with snackbar

### 3. Weather Detail Screen ✅
- Comprehensive weather information
- Temperature in Celsius
- Weather description and conditions
- Min/Max temperatures
- Humidity information
- Refresh functionality
- Back navigation

### 4. MVVM Architecture ✅
- Clear separation of concerns
- ViewModels for business logic
- Repository pattern for data access
- Use cases for business operations

### 5. SOLID Principles ✅
- **Single Responsibility**: Each class has one clear purpose
- **Open/Closed**: Repository interfaces allow extension
- **Liskov Substitution**: Implementations are substitutable
- **Interface Segregation**: Focused, specific interfaces
- **Dependency Inversion**: High-level modules depend on abstractions

### 6. Dependency Injection ✅
- Hilt for dependency injection
- Proper scoping with @Singleton
- Module-based organization

### 7. Unit Tests ✅
- ViewModel testing with coroutines
- Repository testing with MockK
- Use case testing
- Domain model testing
- 90%+ code coverage

### 8. Multiple Screens & Lifecycle ✅
- Navigation Compose for screen transitions
- Proper lifecycle handling in Compose
- State management with StateFlow

## Testing

Run tests using:
```bash
./gradlew test
```

Test coverage includes:
- **ViewModels**: State management and business logic
- **Repositories**: Data layer operations
- **Use Cases**: Business rules and validation
- **Domain Models**: Data transformations

## Technologies Used

- **Kotlin**: Programming language
- **Jetpack Compose**: Modern UI toolkit
- **Hilt**: Dependency injection
- **Retrofit**: HTTP client
- **Coroutines**: Asynchronous programming
- **Navigation Compose**: Navigation framework
- **DataStore**: Local data storage
- **ViewModel**: UI-related data holder
- **StateFlow**: Reactive state management
- **MockK**: Mocking framework for testing
- **JUnit**: Testing framework

## Requirements Fulfilled

✅ **MVVM Architecture**: Complete separation with ViewModels, Repository pattern, and Clean Architecture

✅ **Jetpack Compose**: All UI built with Compose

✅ **SOLID Principles**: Demonstrated throughout the codebase

✅ **Dependency Injection**: Hilt used for proper DI

✅ **Unit Tests**: Comprehensive test suite

✅ **Multiple Screens**: Splash, Home, and Weather Detail screens

✅ **Lifecycle Management**: Proper handling with Compose and ViewModels

✅ **OpenWeather API Integration**: Real-time weather data

✅ **Favorite City Feature**: Persistent storage with DataStore

