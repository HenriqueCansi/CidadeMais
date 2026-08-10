# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

CidadeMais is a native Android app (Java, `obj.cidademais` package) for reporting municipal
issues ("ocorrências" — potholes, lighting, trash, etc.) on a map, backed by Firebase
(Auth + Firestore) and Google Maps/Location services. UI strings and identifiers are in
Portuguese (pt-BR).

## Build / run / test commands

Standard Gradle wrapper commands, run from the repo root:

```
./gradlew assembleDebug          # build debug APK
./gradlew installDebug            # build and install on a connected device/emulator
./gradlew test                    # run JVM unit tests (app/src/test)
./gradlew testDebugUnitTest --tests "obj.cidademais.SomeTest"   # run a single unit test class
./gradlew connectedAndroidTest    # run instrumented tests (app/src/androidTest), needs a device/emulator
./gradlew lint                    # run Android lint
```

On Windows use `gradlew.bat` instead of `./gradlew`.

There is currently no meaningful test suite (only the default `ExampleUnitTest` /
`ExampleInstrumentedTest` stubs), so don't assume test coverage exists for a given feature.

Firebase requires `app/google-services.json` (already present) to build/run; the Google Maps
API key is embedded directly in `AndroidManifest.xml`.

## Architecture

### Single-Activity, hand-rolled "screen stack" (no Fragments/Navigation component)

The app does **not** use Android Fragments, Jetpack Navigation, or multiple Activities beyond
the single launcher `RvActivity`. Instead it uses a small custom framework in the root
`obj.cidademais` package:

- **`RvActivity`** — the one and only `Activity`. Holds a static `__activity` reference (used
  everywhere instead of passing `Context`) and a static `__views` stack
  (`ArrayList<RvView>`). Owns the global loading dialog (`mostrarCarregando` /
  `esconderCarregando`) and forwards Activity lifecycle callbacks
  (`onResume`/`onPause`/`onStart`/`onStop`/`onDestroy`/`onLowMemory`) to whatever screen
  currently holds the map (`frm_Principal_pnlPrincipal.__obj.mapView`).
- **`RvView`** — abstract base class for every "screen" (called a *panel*). Subclasses
  implement `getLayout()` (inflate and cache a `LinearLayout` from an XML layout) and override
  `OnActivated()` (called after the screen becomes visible — wire up listeners here) and
  optionally `OnDisabled()` plus the lifecycle no-ops. `Show()` pushes the view onto
  `RvActivity.__views`, swaps `RvActivity.__activity`'s content view, and calls
  `OnActivated()` after a short delay via `Handler.postDelayed`. `Hide()` pops the view and
  restores the previous one in the stack (re-invoking its `OnActivated()`).
- Each panel is a **singleton accessed through a public static `__obj` field on itself**
  (e.g. `frm_Login_pnlLogin.__obj`, `frm_Principal_pnlPrincipal.__obj`), not through
  dependency injection or constructors. Navigating between screens is just
  `SomeOtherPanel.__obj.Show(); this.Hide();` from inside a click listener. Some panels are
  lazily re-created (`__obj = new X()`) before showing rather than reusing the existing
  singleton — check the call site before assuming a panel's state persists across navigations.
- Some panels define an additional `ShowCustom()` method (not part of `RvView`) for extra
  per-screen setup that must run alongside `Show()` (see `frm_Principal_pnlOcorrencia`,
  `frm_Perfil_pnlPrincipal`) — check for this pattern before adding a new panel type.

### Package/naming convention

Feature areas live under `obj.cidademais.frm_<Feature>/`, each split into `Panel/` (the
`RvView` subclasses, prefixed `frm_<Feature>_pnl<Name>`), `Data/` (POJOs), and `Classe/`
(callback interfaces). Matching layout XML files live in `res/layout/` using the same
lowercase convention (e.g. `frm_Login_pnlLogin` ↔ `res/layout/frm_login_pnllogin.xml`).
Current feature areas: `frm_Login` (auth/signup), `frm_Principal` (map + report an
"ocorrência"), `frm_Perfil_pnlPrincipal` (profile).

Cross-cutting infrastructure lives under `obj.cidademais.Core` (permissions, location) and
`obj.cidademais.Firebase` (Firestore access, split by entity: `Firebase.Ocorrencia`,
`Firebase.Usuario`).

### Permissions

`CmPermissao.solicitarPermissoes()` requests location, camera, and media/storage permissions
in one batch at app start (`RvActivity.onCreate`) and reports back through a single
`Callback` (`onPermitido`/`onNegado`) — there's no per-permission granularity. There is also a
narrower `solicitarLocalizacao()` for requesting just location. Both reuse the same instance
field `callbackAtual`, so only one permission request can be in flight at a time. Media
permission requested depends on SDK level (`READ_MEDIA_IMAGES`/`READ_MEDIA_VIDEO` on
Tiramisu+, plus `READ_MEDIA_VISUAL_USER_SELECTED` on UpsideDownCake+, else
`READ_EXTERNAL_STORAGE`).

### Location

`LocalizacaoManager.buscar()` wraps `FusedLocationProviderClient` + `Geocoder` to resolve the
device's current `CmPosicao` (lat/lng plus reverse-geocoded address/bairro/cidade/estado in
pt-BR). The result is cached in the static `CmPosicao.posicaoAtual` and read by
`frm_Principal_pnlPrincipal.onMapReady` to center the map.

### Firebase access

Firestore access is centralized in small static-method classes under `obj.cidademais.Firebase`
(one per collection: `usuarios`, `ocorrencias`), not through a repository/DAO abstraction.
Each exposes callback interfaces (`onSucesso`/`onErro`) rather than returning `Task`/futures
directly, and screens call these statics inline from click handlers. `Sessao` holds the
current logged-in `Usuario` in a static field as the app's in-memory session state
(`Sessao.isLogado()`, `Sessao.logout()` also signs out of `FirebaseAuth`).

## Working conventions in this codebase

- Java, not Kotlin, throughout `app/src/main/java`.
- Brace style is Allman (opening brace on its own line) in most files — match it in edits.
- Views are found via `findViewById` on the panel's cached `layout`/`getLayout()` inside
  `OnActivated()`; there is no ViewBinding/DataBinding set up.
- When adding a new screen, follow the existing pattern: new `RvView` subclass with a static
  `__obj`, a matching lowercase-named layout XML, listeners wired in `OnActivated()`, and
  navigation via `OtherPanel.__obj.Show(); this.Hide();`.