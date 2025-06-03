# Music Player

A modern Android music player application built with Jetpack Compose that allows you to download and play music from YouTube.

## Features

- 🎵 **YouTube Integration**: Download songs directly from YouTube using yt-dlp
- 🎨 **Modern UI**: Built with Jetpack Compose and Material Design 3
- 🎧 **Audio Playback**: Full-featured media player with playback controls
- 💾 **Local Storage**: Songs are downloaded and stored locally for offline playback
- 🔍 **Search Functionality**: Search and discover music from YouTube
- 🌍 **Internationalization**: Multi-language support with locale configuration
- 🎛️ **Settings**: Customizable app settings and preferences

## Requirements

- **Android API Level**: 33+ (Android 13+)
- **Target SDK**: 35
- **Compile SDK**: 35
- **Java Version**: 11



## Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd MusicPlayer
```

### 2. Configure API Keys

1. Copy `Secrets.example.kt` to `Secrets.kt`
2. Add your YouTube API key (if required)

### 3. Build and Run

```bash
./gradlew assembleDebug
```

Or open the project in Android Studio and run it directly.

## Key Dependencies

- **AndroidX Compose BOM**: Modern UI toolkit
- **Room**: Local database management
- **Ktor**: HTTP client for API calls
- **yt-dlp**: YouTube video/audio downloading
- **FFmpeg**: Audio processing and conversion
- **Coil**: Image loading and caching
- **DataStore**: Preferences storage
- **Navigation Compose**: In-app navigation

## Features in Detail

### YouTube Download
The app uses the yt-dlp library to download audio from YouTube videos. Downloaded songs are stored locally and can be played offline.

### Database Schema
- **Songs Table**: Stores metadata including title, artist, album, duration, cover art, and file paths
- **Favorites**: Track user's favorite songs
- **Room Database**: Provides type-safe database access

### Audio Playback
- Background audio playback service
- Media session controls
- Notification controls for background playback
- Support for various audio formats

### Modern UI
- Material Design 3 theming
- Dynamic color support
- Responsive layouts for different screen sizes
- Smooth animations and transitions

## Permissions

The app requires the following permissions:
- `INTERNET`: For downloading songs from YouTube
- `FOREGROUND_SERVICE`: For background audio playback
- `FOREGROUND_SERVICE_MEDIA_PLAYBACK`: Specific media playback permission
- `POST_NOTIFICATIONS`: For playback notifications

## Building for Release

1. Configure signing in `app/build.gradle.kts`
2. Build release APK:

```bash
./gradlew assembleRelease
```


## Disclaimer

This app is for educational purposes. Please respect YouTube's Terms of Service and copyright laws when downloading content.
