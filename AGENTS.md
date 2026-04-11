# AGENTS Guide for Meraki

## Project shape

- Single-module Android app (`:app`) configured in `settings.gradle.kts`; Kotlin + XML + ViewBinding, no Compose.
- Entry flow is `SplashActivity -> LoginActivity/SignupActivity -> MainActivity` (`app/src/main/AndroidManifest.xml`, `app/src/main/java/com/anantmittal/meraki/activities`).
- Main UI is Navigation Component-driven: `activity_main.xml` hosts `FragmentContainerView` with `@navigation/nav_graph`.
- Core product loop: browse Unsplash photos in `Wallpapers`, open `SetWallpaper`, then set/download/share/favorite.

## Runtime integrations

- Unsplash API calls are centralized in `api/ApiInterface.kt` + `api/RetrofitBuilder.kt`.
- Auth header uses `BuildConfig.client_id`; Firebase API key uses `BuildConfig.current_key` (`app/build.gradle.kts`).
- Both keys are loaded from `local.properties`; never hardcode or commit replacements.
- Firebase Realtime Database URL is duplicated in `data_modals/AppUserInfo.kt` (`refUrl`) and `MerakiApp.kt` (`FirebaseOptions`). Keep them in sync if changed.

## Data and navigation patterns

- Cross-fragment payloads are passed via `Bundle` + `Serializable` `OwnerData` (see `Wallpapers.kt`, `Downloads.kt`, `Favourites.kt`, `Uploads.kt` -> `SetWallpaper.kt`).
- Navigation uses explicit action IDs from `app/src/main/res/navigation/nav_graph.xml` (example: `action_wallP_to_setWallpaper`).
- User-scoped Firebase tree shape is:
    - `users/{uid}/user_creds`
    - `users/{uid}/downloads`
    - `users/{uid}/favourites`
    - `users/{uid}/uploads`
- `SetWallpaper.kt` is the write hub: toggles favorites and records downloads.

## Codebase conventions to follow

- Keep feature code in existing package split: `activities`, `fragments`, `adapters`, `api`, `data_modals`.
- Existing async style is Retrofit `Call.enqueue(...)` + Firebase listeners (no coroutines/Flow yet); stay consistent unless refactoring broadly.
- RecyclerViews are grid-based and adapter callbacks pass clicked model objects (`WallpaperAdapter`, `ImageAdapter`).
- Project-wide log tag convention currently uses `TAG = "xyz"` from `MainActivity.kt` and local `val TAG` in fragments.
- UI uses ViewBinding everywhere (`FragmentXxxBinding`, `ActivityXxxBinding`), not `findViewById` for layout widgets.

## Build, test, and debug workflow

- Use Gradle wrapper from project root.
- Debug APK build:
    - `./gradlew.bat :app:assembleDebug`
- Unit tests (currently only template test exists):
    - `./gradlew.bat :app:testDebugUnitTest`
- Instrumented tests:
    - `./gradlew.bat :app:connectedDebugAndroidTest`
- Lint:
    - `./gradlew.bat :app:lintDebug`

## Practical guardrails for agents

- Do not change `local.properties` values in commits; treat `client_id` and `current_key` as local secrets.
- If you modify Firebase schema paths, update all readers/writers in `SignupActivity`, `Profile`, `Downloads`, `Favourites`, `Uploads`, and `SetWallpaper` together.
- If you alter navigation IDs or destinations, update both `nav_graph.xml` and all `findNavController().navigate(...)` callsites.
- Preserve current permissions/provider behavior in `AndroidManifest.xml` when touching download/share features (`FileProvider`, storage/media permissions).
