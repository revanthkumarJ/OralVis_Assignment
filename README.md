# OralVis - Session-based Camera App

### Done In:
2 days

## Demonstartion Video
https://drive.google.com/file/d/1XT3z69wLBlY_yV2LIbqzaFbFwCi5nsHw/view?usp=sharing

## Objective
OralVis is a mobile application that allows users to capture images session-wise using the phone’s camera. Each session has associated metadata and images stored locally, with the ability to search and view past sessions.

**Key Features:**
- Capture images using the built-in camera.
- Organize captured images by sessions.
- End each session by entering metadata: `SessionID`, `Name`, and `Age`.
- Store metadata in a local SQLite database.
- Save images in app-specific storage under folders per session.
- Search by `SessionID` to view session details and images.

---

## Requirements
- **Start Session:** User creates a session and captures multiple images.
- **End Session:** User fills metadata (`SessionID`, `Name`, `Age`).
- **Storage:**
  - Metadata → Stored in SQLite via Room.
  - Images → Stored under:  
    `Android/media/OralVis/Sessions/<SessionID>/IMG_timestamp.jpg`
- **Search Functionality:** Search by `SessionID` to access metadata and images.

---

## Technology Stack
- **Kotlin** with **Jetpack Compose** for UI.
- **Koin** for dependency injection.
- **Room** (SQLite) for local storage.
- **Coil** for efficient image loading.
- **CameraX** for camera functionality.
- **MVVM Architecture** for clean code separation.

---
