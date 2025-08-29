# ReadBooks - Modern Android E-Book Reader

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin Version](https://img.shields.io/badge/Kotlin-1.9.22-blue.svg)](https://kotlinlang.org)

## Features

-   **Local Library Management:**
    -   [x] Add and manage books stored on the device.
    -   [x] Grid and List view modes.
    -   [x] Advanced sorting (by title, author, date added, last read).
    -   [x] Intuitive filtering by reading status (`Unread`, `In Progress`, `Finished`) via one-tap filter chips.
    -   [x] Track and display reading progress visually.
-   **Online Book Discovery:**
    -   [x] Browse and discover new books from the Gutendex API.
    -   [x] View popular books and browse by genre.
    -   [x] Performant, debounced search functionality.
-   **Integrated Reading Experience:**
    -   [x] A seamless and immersive reader powered by the Readium toolkit.
    -   [x] Navigate via the table of contents.
-   **Modern User Experience:**
    -   [x] Clean, intuitive, and animated UI built entirely with Jetpack Compose.
    -   [x] Responsive design for various screen sizes.
    -   [x] Light and Dark theme support.
 
## Architecture & Tech Stack

This project serves as a showcase of professional, modern Android application architecture.

### Core Principles

-   **Clean Architecture:** Strictly follows the principles of Clean Architecture, separating the codebase into `ui`, `domain`, `data`, and `di` layers for maximum testability and maintainability.
-   **Unidirectional Data Flow (UDF):** State flows downwards from the data layer to the UI, and events flow upwards from the UI to the ViewModels, creating a predictable and debuggable state management system.
-   **Reactive Programming:** The app is fully reactive. Data is exposed from repositories upwards using Kotlin `Flow`. UI state is managed in ViewModels using `StateFlow`, created by `combine`-ing underlying data streams.
-   **Modularization:** The project is modularized by architectural layer, enforcing separation of concerns and improving build times.

### Technology Stack

-   **UI:** 100% [Jetpack Compose](https://developer.android.com/jetpack/compose) for declarative UI development.
-   **Dependency Injection:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) for managing dependencies across the app.
-   **Asynchronous Programming:** [Kotlin Coroutines & Flows](https://kotlinlang.org/docs/coroutines-overview.html) for all asynchronous operations.
-   **Database:** [Room](https://developer.android.com/training/data-storage/room) for robust, local persistence with reactive query support.
-   **Networking:** [Retrofit](https://square.github.io/retrofit/) for type-safe HTTP requests, with [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) for efficient JSON parsing.
-   **Pagination:** [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview) for efficiently loading and displaying paginated data from the network.
-   **Navigation:** [Jetpack Navigation for Compose](https://developer.android.com/jetpack/compose/navigation) for navigating between screens.
-   **Architecture Components:** ViewModel, Lifecycle.
