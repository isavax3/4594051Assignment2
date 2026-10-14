# StudentIDAssignment2 - NIT3213 Final Assignment

This is my Android app for the NIT3213 final assignment. It logs in to the `nit3213api` with my student ID and
first name, gets my topic's list of entities and shows them in a RecyclerView. Tapping an item opens a details
screen with everything about it, including the description.

It's written in Kotlin with XML layouts and uses Hilt for dependency injection, Retrofit + Moshi for the API calls,
ViewModels with StateFlow, and JUnit + MockK for the unit tests. The look (cream background, red buttons, Inter font)
is carried over from my assignment 1 app.

## The 3 screens
| Screen | What it does | Files |
|---|---|---|
| Login | Student ID and first name fields. Sends a POST to `/footscray/auth` and shows an error if it doesn't work | `LoginActivity`, `LoginViewModel`, `activity_login.xml` |
| Dashboard | Uses the keypass from the login to GET `/dashboard/{keypass}` and shows each entity in a RecyclerView (without the description) | `DashboardActivity`, `DashboardViewModel`, `EntityAdapter`, `item_entity.xml` |
| Details | Shows every property of the entity that was tapped, plus its description | `DetailsActivity`, `DetailsViewModel`, `activity_details.xml` |

## How to build and run
What you need:
- Android Studio with support for Android Gradle Plugin 9.0 (I used the same setup as assignment 1)
- An emulator or phone running Android 8.0 (API 26) or newer, with internet access

Steps:
1. Clone the repo: `git clone <repo link>` (or download it as a zip and unzip it)
2. In Android Studio go to File > Open and pick the project folder
3. Wait for the Gradle sync to finish. The first sync downloads all the libraries so it can take a few minutes
4. Pick an emulator or phone and press Run

The project uses Gradle 9.1 through the Gradle wrapper, so you don't need to install Gradle yourself.
If Android Studio asks about the Gradle JDK, the JDK that comes with Android Studio works.

## Logging in
- **Student ID:** your student number without the "s", e.g. `1234567` for s1234567
- **First name:** your first name exactly how the uni has it. It's case sensitive, so `Alex` works but `alex` doesn't

The API is on a free server that goes to sleep when nobody is using it, so the first login can take up to a minute.
The app waits for it (the timeout is 60 seconds) and shows a message while it's loading.

My class is Footscray so the app uses `/footscray/auth`. To use another class location, change `CLASS_LOCATION` in
`data/remote/ApiConfig.kt` to `sydney` or `br`.

## Running the unit tests
- In Android Studio: right click the `app/src/test` folder and pick "Run 'Tests in ...'"
- Or in the terminal: `./gradlew testDebugUnitTest` (on Windows: `gradlew.bat testDebugUnitTest`)

The tests don't need an emulator or internet because the API and repositories are mocked with MockK.

## How the app is organised
```
com.example.assignment2
├── Assignment2App.kt          @HiltAndroidApp application class
├── data
│   ├── remote                 ApiService (Retrofit), ApiConfig and the request/response classes (dto)
│   ├── model                  Entity, EntityField, Dashboard and ApiResult
│   ├── mapper                 EntityMapper turns the raw json map into an Entity
│   └── repository             AuthRepository, EntityRepository and their implementations
├── di                         Hilt modules (NetworkModule, RepositoryModule) and the ClassLocation qualifier
└── ui
    ├── common                 edge to edge helpers and the error messages
    ├── login                  LoginActivity, LoginViewModel, LoginUiState
    ├── dashboard              DashboardActivity, DashboardViewModel, DashboardUiState, EntityAdapter, EntityViewHolder
    └── details                DetailsActivity, DetailsViewModel, EntityExtras
```

Each screen follows the same pattern (MVVM):
- The **Activity** only shows the state and passes button taps to the ViewModel
- The **ViewModel** holds the screen state in a `StateFlow` and calls the repository in `viewModelScope`
- The **repository** calls the API and returns an `ApiResult` (Success or Error) instead of throwing, so errors are
  handled in one place (`safeApiCall`)

Because every topic has different property names, the dashboard response is read as a list of maps instead of a fixed
class. `EntityMapper` turns each one into an `Entity`: the description is kept separate, the property names are made
readable (`artistName` becomes "Artist name") and whole numbers don't show ".0". The first property is used as the
title of each card. This means the app works with whatever topic the keypass gives.

## Dependency injection (Hilt)
- `Assignment2App` has `@HiltAndroidApp`, which sets up Hilt for the whole app
- The 3 activities have `@AndroidEntryPoint` and get their ViewModels with `by viewModels()`
- The 3 ViewModels have `@HiltViewModel` and get their dependencies through `@Inject constructor`
- `NetworkModule` uses `@Provides` for the things that come from libraries (Moshi, OkHttpClient, Retrofit and the
  ApiService). They're `@Singleton` so the whole app shares one of each
- `RepositoryModule` uses `@Binds` to tell Hilt which class to use for the `AuthRepository` and `EntityRepository`
  interfaces
- `EntityMapper` and the repository implementations have `@Inject constructor`, so Hilt can build them without a module
- `@ClassLocation` is a qualifier so Hilt knows which String to inject as the class location
- The keypass and the selected entity get to the ViewModels through `SavedStateHandle`, which Hilt fills with the
  intent extras

Because the ViewModels and repositories get their dependencies passed in, the tests can pass in mocks instead.

## Unit tests
| Test class | What it checks |
|---|---|
| `LoginViewModelTest` | loading then success with the keypass, input checks (empty fields, "s" in the ID), API errors, no double login, resetting the state |
| `DashboardViewModelTest` | loads entities with the keypass on start, error state, "Try again" after an error, missing keypass |
| `DetailsViewModelTest` | rebuilds the entity from the intent extras, handles missing or broken extras |
| `AuthRepositoryImplTest` | sends the right request to `/footscray/auth`, turns 400/401/500 and no internet into the right errors |
| `EntityRepositoryImplTest` | maps the dashboard response, falls back to the list size, 404 and bad json |
| `EntityMapperTest` | description handling, field order, readable labels and number formatting |
| `EntityAdapterTest` | the RecyclerView adapter's item count and refreshing the list |

The ViewModel tests use `MainDispatcherRule` to swap `Dispatchers.Main` for a test dispatcher, and `runTest` with
`advanceUntilIdle()` to control the coroutines.

## API
Base URL: `https://nit3213apinew.onrender.com/`

| Endpoint | Method | Body / response |
|---|---|---|
| `/footscray/auth` | POST | `{"username": "<student id>", "password": "<first name>"}` gives back `{"keypass": "<topic>"}` |
| `/dashboard/{keypass}` | GET | `{"entities": [ ... ], "entityTotal": 7}` |

The OkHttp logger is set to BASIC so Logcat only shows the URL and status code, not the request body with the
student ID and name.

## Libraries
| Library | Version | Used for |
|---|---|---|
| Android Gradle Plugin | 9.0.0 | building the app (with its built-in Kotlin) |
| Hilt (Dagger) | 2.59.2 | dependency injection |
| KSP | 2.3.6 | running Hilt's annotation processor |
| Retrofit + converter-moshi | 2.11.0 | API calls |
| Moshi (moshi-kotlin) | 1.15.2 | turning the json into Kotlin objects |
| OkHttp logging-interceptor | 4.12.0 | logging the API calls in Logcat |
| AndroidX Lifecycle | 2.9.4 | ViewModel, viewModelScope, lifecycleScope |
| RecyclerView | 1.4.0 | the dashboard list |
| Material Components | 1.12.0 | text fields, buttons, cards, progress indicators |
| JUnit | 4.13.2 | unit tests |
| MockK | 1.13.12 | mocking in the unit tests |
| kotlinx-coroutines-test | 1.10.2 | testing the coroutines in the ViewModels |

All the versions are in `gradle/libs.versions.toml`.

## Credits
The Inter font and the Lucide icons are open source. Their licences are in `THIRD_PARTY_LICENSES.md`.
