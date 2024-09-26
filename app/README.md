CryptoTracker

CryptoTracker is a cryptocurrency tracking app that provides users with real-time information on crypto trading pairs. The app retrieves live crypto data, including the latest price, daily percentage change, and volume, and displays it in an user-friendly interface.
🚀 Features

    Displays live data for various crypto trading pairs (e.g., BTCUSD, ETHUSD).
    Updates price and daily percentage change every 5 seconds.
    Real-time filtering of cryptocurrencies with a search bar.
    Network error handling with offline notifications.
    Clean, modern UI using Material 3 design guidelines.

🛠️ Technologies Used

    Kotlin: The primary programming language.
    Jetpack Compose: For building declarative UI components.
    Hilt: For dependency injection.
    Retrofit: For making HTTP network requests.
    Coroutines: For managing asynchronous tasks.
    Flow: For reactive streams and state management.
    Material 3: For UI components that follow Material Design guidelines.
    MockK: For unit testing with mocks.

📐 Architecture

The app follows the MVI (Model-View-Intent) architecture pattern to ensure a clear separation of concerns and easy testability.

    Model: Represents the application state. All state changes happen via immutable data structures.
    View: Displays the current state of the Model and sends Intents based on user actions.
    Intent: Represents the user's actions and triggers updates in the Model.


Key Components:

    HomeViewModel:
        Manages the app state and handles intents like loading tickers or searching.
        Uses StateFlow to emit UI states and reacts to network changes.

    CryptoRepository:
        Interfaces with the API to fetch cryptocurrency data via Retrofit.

    CryptoTicker:
        A data model representing individual crypto ticker data.

    UI:
        Built with Jetpack Compose.
        The app's design follows Material 3 guidelines (in general :)).

🔧 Setup Instructions
Prerequisites:

    Android Studio (latest version).
    Android SDK version 29 or later.
    Kotlin 1.5 or later.

Steps to Run the Project:

    Clone the repository:

    bash

    git clone https://github.com/your-username/crypto-tracker.git

    Open the project in Android Studio.

    Sync the project to download all the dependencies.

    Build and run the app on an emulator or physical device.

🌐 API

We are using the Bitfinex API to fetch real-time data for cryptocurrency tickers. The relevant endpoint for fetching ticker data is:

    GET /v2/tickers - Fetches data for multiple tickers in one request.

Example API Call:

bash

GET https://api-pub.bitfinex.com/v2/tickers?symbols=tBTCUSD,tETHUSD,tXRPUSD

The response includes key data fields such as the last price, daily change, and volume for each trading pair.

🧪 Testing

Unit tests are written to ensure the core functionality of the app. The test suite includes:

    ViewModel Unit Tests: Ensuring that the HomeViewModel properly handles intents, updates the state, and handles network errors.
    Data Model Unit Tests: Testing the CryptoTicker data model and it's formatting functions.

Run tests using the following command:

bash

./gradlew test

📱 Screenshots

