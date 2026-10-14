OPSC6312-POE-Part-2-Pok-mon-Explorer

# Pokémon Explorer

An Android Pokédex companion — browse, search, compare and collect Pokémon from
[PokéAPI](https://pokeapi.co), wrapped in a game-UI look inspired by the in-game
Pokédex and Pokémon trading cards.

Built with **Kotlin** and **Jetpack Compose** (Material 3).

[![Android CI](https://github.com/MJjimmy/Pok-mon/actions/workflows/android-ci.yml/badge.svg)](https://github.com/MJjimmy/Pok-mon/actions/workflows/android-ci.yml)

The full design brief lives in [`design/pokemon-explorer-design.md`](design/pokemon-explorer-design.md).

---

## Screens

| Screen | What it does |
| --- | --- |
| **Splash** | Cinematic Poké Ball open, logo and tagline |
| **Explore** | Featured hero Pokémon, Random Discovery, type filter chips, 2-column card grid |
| **Search** | Debounced lookup by name or Pokédex number, with popular searches and an empty state |
| **Detail** | Hero artwork on a type gradient, Normal/Shiny toggle, height/weight, abilities, animated base stats, moves, favourite / catch / compare actions |
| **Compare** | Head-to-head VS arena, inline pickers, per-stat winner highlighting and base-stat totals |
| **Collection** | Pokédex completion ring over Favourites / Caught grids |
| **Type Guide** | All 18 types with strong / weak / resistant / immune breakdowns |
| **Settings** | Appearance, data, preferences and about sections |

## Building

Requires **JDK 17** and the Android SDK (compileSdk 34).

```bash
# Open the project root in Android Studio and press Run, or:
./gradlew assembleDebug
```

CI builds the debug APK and runs the unit tests on every push
(`.github/workflows/android-ci.yml`), so the project is known to compile.

> **Note:** `gradle/wrapper/gradle-wrapper.jar` is not committed in this checkout.
> If `./gradlew` reports a missing wrapper, either open the project in Android
> Studio (it will restore the wrapper) or generate it once with a local Gradle:
> `gradle wrapper --gradle-version 8.9`. The intended Gradle version is pinned in
> `gradle/wrapper/gradle-wrapper.properties`. CI installs Gradle 8.9 directly for
> the same reason.

### Toolchain

Versions are centralised in `gradle/libs.versions.toml`.

| | |
| --- | --- |
| Android Gradle Plugin | 8.5.2 |
| Kotlin | 2.0.20 (Compose compiler plugin) |
| Gradle | 8.9 |
| Compose BOM | 2024.09.00 |
| minSdk / targetSdk / compileSdk | 24 / 34 / 34 |

## Project layout

```
app/src/main/java/com/pokemon/explorer/
├── MainActivity.kt          Single activity: splash → app shell, back handling
├── data/
│   ├── Models.kt            PokemonSummary, PokemonDetail, Stat, Ability, Sprites
│   ├── TypeData.kt          Type colours, contrast helper, full 18-type matchup chart
│   └── PokeApi.kt           Dependency-free PokéAPI client + in-memory response cache
├── state/
│   └── AppState.kt          ViewModel: per-tab back stacks, favourites, caught, theme
└── ui/
    ├── AppRoot.kt           Screen host + bottom navigation
    ├── theme/Theme.kt       Palette, spacing, radii, typography, type gradients
    ├── components/          Pokéball, badges, stat bars, cards, buttons, icons, skeletons
    │                        (ComponentPreviews.kt previews them all in the IDE)
    └── screens/             One file per screen, listed above
```

Unit tests live in `app/src/test/java/com/pokemon/explorer/` and cover the
hand-transcribed type chart. Run them with `./gradlew testDebugUnitTest`.

### Architecture notes

- **Navigation** is a stack per bottom-nav tab held in `AppState`, so each tab
  remembers where you were and Back walks that tab's history. No navigation
  library — it is a direct port of the original context-based approach.
- **Networking** uses `HttpURLConnection` and `org.json` (both part of the
  platform) rather than Retrofit, so there is no networking dependency to
  configure. Responses are memoised for the life of the process.
- **Images** are loaded with [Coil](https://coil-kt.github.io/coil/) from the
  official-artwork sprite set. Normal and shiny artwork both come from one
  helper, `PokeApi.artworkUrl(id, shiny)`.
- **Icons** are drawn on a Compose `Canvas` in `ui/components/AppIcons.kt`,
  reproducing the original hand-authored SVG shapes, so there is no icon-font
  dependency.
- **Loading states** are modelled as a `LoadState` sealed interface
  (`Loading` / `Success` / `Failure`) with a `rememberLoadState` helper, so a
  screen can never render half-loaded.
- **Touch targets** follow the brief's 48dp minimum. Controls such as
  `CircleIconButton` and `ToggleSwitch` keep their designed visual size and pad
  their hit area out to `MinTouchTarget` in `ui/components/Common.kt`.

### Previewing in Android Studio

`ui/components/ComponentPreviews.kt` covers every public component, and
`ui/screens/ScreenPreviews.kt` covers the three screens that hold no network state
(Type Guide, Settings, Splash), so those render at full fidelity in the preview
pane. The data-driven screens — Explore, Search, Detail, Compare, Collection —
only ever show their loading state under layoutlib, because PokéAPI never resolves
in the preview renderer. Run the app on a device to check those.

## Differences from the original web prototype

This app replaces an earlier React/Vite prototype. Deliberate changes:

- **No phone frame.** The prototype simulated a 390×844 device inside the
  browser. On Android the app *is* the device, so the mock frame, fake status bar
  and dynamic island are gone; the real system bars are used via edge-to-edge.
- **State survives rotation.** Collection and navigation state live in a
  `ViewModel`; the browser version lost them on reload.
- **Compare pickers accept names.** The prototype's placeholder read "Name or #"
  but only parsed integers. Here a name is resolved through the API.
- **Reset Collection and Clear Cache are wired up.** They were inert buttons in
  the prototype.

## Known gaps

- **Light theme is not implemented.** The design brief specifies a single dark
  "Pokédex at night" look. Settings stores the System/Light/Dark preference but
  only the dark palette exists, so switching has no visual effect yet.
- **Searches are exact, by design.** PokéAPI's `/pokemon/{name}` endpoint returns
  a single match, so typing "pika" finds nothing until "pikachu" is complete —
  same behaviour as the prototype. Fuzzy or prefix search would need the full
  name index from `/pokemon?limit=1025`.
- **No on-device persistence.** Favourites, caught Pokémon and preferences reset
  when the app process is killed. Adding Room or DataStore is the natural next
  step.
- **Bundled fonts were not carried over.** The design uses Nunito, Outfit and
  JetBrains Mono; the app currently falls back to platform families (see
  `DisplayFont` / `BodyFont` / `MonoFont` in `ui/theme/Theme.kt`). Drop the TTFs
  into `app/src/main/res/font/` and swap those three values to match the design
  exactly.

## Attribution

Pokémon data from [PokéAPI](https://pokeapi.co). Sprites from the
[PokeAPI sprites](https://github.com/PokeAPI/sprites) repository.

Pokémon and all related names are trademarks of Nintendo / Game Freak / Creatures
Inc. This is a non-commercial fan project, not affiliated with The Pokémon
Company.

## GitHub Link

https://github.com/Sheketli/OPSC6312-POE-Part-2-Pok-mon-Explorer.git

## Video Presentation YouTube Link

https://youtu.be/2-7nq8ZJ5-I
