# CLAUDE.md --- Spec-Driven DDD -> TDD -> BDD -> Clean Architecture Pipeline

## Overview

**Project**: PixelFort --- Pixel Art Tower Defense Android Game
**Tech stack**: Kotlin 2.2.10, Jetpack Compose (BOM 2025.12.00), Material 3, Hilt, Room, DataStore, Coroutines/Flow, Canvas for game rendering
**Goal**: Publish on Google Play

Each feature is developed through a rigorous, spec-driven methodology:
**Read Spec -> Branch -> DDD -> (TDD -> BDD) -> Clean Architecture -> Test -> Review -> Commit -> Push**

---

## Spec-Driven Development Workflow

All features are defined in `spec/` (git-ignored, local only).
Each spec file (`SPEC-NNN-*.md`) contains: requirements, DDD analysis, TDD/BDD test specs, clean architecture mapping, and acceptance criteria.

### Workflow Steps

```
1. Read spec/SPEC-NNN-*.md -> confirm requirements
2. Create branch: git checkout -b feature/NNN-short-name
3. Enter DDD -> (TDD -> BDD) -> Clean Architecture loop:
   a. DDD: Identify domain changes (entities, value objects, events)
   b. TDD: Write failing tests -> implement -> refactor
   c. BDD: Verify Given/When/Then scenarios pass
   d. Clean Architecture: Ensure layer boundaries respected
4. All tests pass -> ask user: "Does this meet the spec requirements?"
5. User confirms -> commit with descriptive message + push to GitHub
6. Update spec/README.md: mark SPEC-NNN as COMPLETED
7. Wait for user to specify next spec (or new spec version)
8. Go to 1
```

### Rules
- **NEVER skip the TDD phase** --- write failing test first, then implement
- **NEVER proceed with red tests** --- fix before moving on
- **NEVER merge without user approval** of acceptance criteria
- If a spec needs changes, update the spec file first, then re-implement
- Branch naming: `feature/NNN-short-name` (e.g. `feature/012-speed-wave-preview`)

---

## How to Run the Pipeline

When the user says **"run pipeline"**, **"new feature"**, **"implement SPEC-NNN"**, or **"next spec"**:

1. Read the spec file from `spec/`
2. Create the feature branch
3. Execute the DDD -> TDD -> BDD -> Clean Architecture loop
4. If a phase fails validation, loop back --- do not proceed

---

## Development Methodology

### DDD (Domain-Driven Design)
- **Core Domain**: `engine/` --- Game logic (towers, enemies, projectiles, waves, maps)
- **Supporting Domain**: `feature/progress/` --- Save/load progress
- **Generic Domain**: `core/` --- Audio, database, settings
- **Ubiquitous Language**: Tower, Enemy, Wave, Projectile, Grid, Cell, Path, Gold, Lives
- **Aggregate Root**: `GameEngine` is the core aggregate root, all state changes go through it
- **Value Objects**: `GridPoint`, `TowerStats`, `TowerType`, `EnemyType`
- **Entities**: `Tower`, `Enemy`, `Projectile` (with unique IDs)
- **Domain Events**: `EnemyKilled`, `WaveCompleted`, `GameOver`, `TowerPlaced`

### TDD (Red-Green-Refactor)
Every feature follows this cycle:
1. **Red**: Write a failing test first
2. **Green**: Write the minimum code to pass
3. **Refactor**: Clean up while keeping tests green
4. **On failure**: Test fails -> analyze cause -> fix -> re-run tests
- **NEVER write production code without a failing test first**

### BDD (Behavior-Driven Development)
Use Given-When-Then format to define behavior specifications:
```
Feature: Tower Defense
  Scenario: Tower attacks enemy in range
    Given a level 1 archer tower at (3,4)
    And a goblin within the tower's range
    When the tower's cooldown expires
    Then the tower fires a projectile at the goblin
    And the tower's cooldown resets
```

### Clean Architecture Layers
```
engine/ (Domain Layer)     <- Pure Kotlin, ZERO Android imports
    ^
feature/*/viewmodel/       <- Use Cases + Presentation
    ^
feature/*/ui/              <- UI Layer (Compose)
    ^
core/                      <- Infrastructure (Room, DataStore, Sound)
```
Dependency direction: outer -> inner. Inner layers know nothing about outer layers.

---

## Project Structure

```
com.pixelfort.towerdefense/
├── PixelFortApplication.kt
├── MainActivity.kt
│
├── engine/                          ★ Core Domain - Pure Kotlin
│   ├── GameEngine.kt                Aggregate Root: update(deltaMs), processAction(), snapshot()
│   ├── GameState.kt                 sealed: Loading, Playing, Paused, Won, Lost
│   ├── GameSnapshot.kt              Immutable snapshot for UI consumption
│   ├── GameConfig.kt                Game balance constants
│   ├── model/
│   │   ├── GridCell.kt, GameMap.kt
│   │   ├── Tower.kt, TowerType.kt, TowerStats.kt
│   │   ├── Enemy.kt, EnemyType.kt
│   │   ├── Projectile.kt
│   │   ├── Wave.kt, WaveGroup.kt
│   │   └── PlayerState.kt
│   ├── event/
│   │   ├── GameEvent.kt             sealed: EnemyKilled, WaveCompleted, TowerPlaced, LivesLost, GameWon, GameLost
│   │   └── GameEventBus.kt          Collects events per frame
│   ├── system/
│   │   ├── WaveSpawnerSystem.kt
│   │   ├── EnemyMovementSystem.kt
│   │   ├── TowerTargetingSystem.kt
│   │   ├── ProjectileSystem.kt
│   │   ├── EnemyDeathSystem.kt
│   │   └── EndReachSystem.kt
│   ├── action/
│   │   ├── GameAction.kt            sealed: PlaceTower, UpgradeTower, SellTower, StartWave
│   │   └── ActionProcessor.kt
│   └── level/
│       ├── LevelDefinition.kt
│       └── Levels.kt                Level1, Level2, Level3
│
├── feature/
│   ├── game/
│   │   ├── ui/                      GameScreen, GameCanvas, HudOverlay, PauseMenu, GameOverOverlay
│   │   ├── renderer/                MapRenderer, TowerRenderer, EnemyRenderer, ProjectileRenderer
│   │   ├── viewmodel/               GameViewModel, GameUiState
│   │   ├── vfx/                     Particle, ParticleEmitter, ParticlePool, ScreenShake
│   │   └── animation/               SpriteAnimation, AnimationState, TweenAnimator
│   ├── menu/                        MainMenuScreen
│   ├── levelselect/                 LevelSelectScreen
│   ├── settings/                    SettingsScreen
│   └── progress/
│       ├── data/                    ProgressRepositoryImpl
│       └── domain/                  ProgressRepository interface, LevelProgress
│
├── core/
│   ├── database/                    Room DB, DAO, Entity
│   ├── datastore/                   SettingsDataStore
│   ├── audio/                       SoundManager
│   ├── di/                          Hilt Modules
│   └── util/                        GridMath, PixelScaler
│
└── navigation/                      AppNavGraph, Routes
```

---

## Phase-by-Phase Development Plan (TDD Driven)

### Phase 1: Project Skeleton ✅
- Android Studio Empty Compose Activity project
- Version Catalog, Gradle plugins, package structure
- Application class + MainActivity stub

### Phase 2: Domain Models (TDD)
TDD in order:
1. `GridCell`, `GridPoint`, `CellType` -> test grid coordinate math
2. `GameMap` -> test path validity
3. `TowerType`, `TowerStats` -> test level stat scaling
4. `Tower` -> test build/upgrade cost
5. `EnemyType`, `Enemy` -> test HP, speed
6. `Projectile` -> test movement vector
7. `Wave`, `WaveGroup` -> test wave definitions
8. `PlayerState` -> test gold, lives changes
9. `LevelDefinition` + `Level1` -> test level data integrity

### Phase 3: Game Systems (TDD + BDD)
Each System defined with BDD specs, implemented with TDD:

1. **EnemyMovementSystem**
   - Given enemy at path start -> When update -> Then pathProgress increases
   - Given enemy reaches end -> When update -> Then pathProgress = 1.0

2. **TowerTargetingSystem**
   - Given tower cooldown expired + enemy in range -> When update -> Then produces Projectile
   - Given no enemy in range -> When update -> Then does not fire

3. **ProjectileSystem**
   - Given projectile near target -> When update -> Then deals damage and removes projectile
   - Given target already dead -> When update -> Then removes projectile

4. **WaveSpawnerSystem**
   - Given wave started -> When interval elapsed -> Then spawns enemy

5. **EnemyDeathSystem**
   - Given enemy HP <= 0 -> When update -> Then removes enemy + awards gold

6. **EndReachSystem**
   - Given enemy reaches end -> When update -> Then reduces lives

### Phase 4: GameEngine Integration (TDD)
1. **ActionProcessor** -> test tower place/upgrade/sell validation
2. **GameEventBus** -> test event collection and dispatch
3. **GameEngine** -> integration test: full game loop from wave spawn to victory/defeat

### Phase 5: UI Rendering - MVP
- GameCanvas: Canvas composable drawing all game elements (colored blocks first)
- GameViewModel: `withFrameMillis` driven game loop
- GameScreen: Canvas + HUD overlay
- Tap to place tower
- **Verify**: playable single-level prototype

### Phase 6: HUD and Game Flow
- Tower selection bar, upgrade/sell popup
- Pause/resume, game over/victory screens
- Wave start button

### Phase 7: Menus, Navigation, Persistence
- Navigation Compose routing
- Main menu, level select, settings
- Room save progress, DataStore settings

### Phase 8: Audio
- SoundPool management
- Game events trigger sound effects

### Phase 9: Pixel Art Basics
- SpriteSheet system: load and slice sprite sheets (PNG)
- Character sprites: 3 levels per tower + unique enemy designs
- Map tile set: path, grass, obstacle, decoration tiles
- Basic animations: tower attack (3-4 frames), enemy walk (4 frames)

### Phase 10: VFX System
- **Particle System**: Particle, ParticleEmitter, ParticlePool, ParticleRenderer
- **Effects**:
  - Enemy death explosion particles (color varies by enemy type)
  - Tower attack fire/glow effects
  - Projectile trail effects
  - Gold collection floating text (+10g)
  - Damage number floating display
  - Tower upgrade flash effect
  - Tower placement smoke effect
  - Wave start/end full-screen notification animation
  - Victory fireworks/star particles
  - Boss entrance screen shake

### Phase 11: Advanced Art and Animation
- **Environment animations**: grass sway, water ripple, optional day/night cycle, optional weather
- **UI animations**: button press feedback, pixel transition effects, HUD value rolling numbers, lives lost red flash
- **Character advanced animations**: hurt flash white, boss attack animation, tower idle animation

### Phase 12: Content and Balance
- Level 2, Level 3 design (different terrain themes: forest, desert, snow)
- Wave difficulty tuning
- Economy balance
- Per-map independent tile set and color palette

### Phase 13: Polish and Release
- Tutorial guide (first-play animated guide)
- Loading screen pixel animation
- App icon (pixel art)
- R8/ProGuard
- Play Store assets (screenshots, description, feature graphic)

---

## Game Core Design

### Tower Types
| Type | Damage | Range | Speed | Cost | Feature |
|------|--------|-------|-------|------|---------|
| ARCHER | Medium | Far | Fast | 100g | Single target precision |
| CANNON | High | Medium | Slow | 150g | Area damage |
| MAGIC | Low | Medium | Medium | 200g | Slow effect |

### Enemy Types
| Type | HP | Speed | Reward |
|------|-----|-------|--------|
| GOBLIN | Low | Fast | 10g |
| ORC | High | Slow | 25g |
| DRAGON | Very High | Medium | 50g |

### Game Loop Architecture
```
GameViewModel (withFrameMillis)
  -> GameEngine.update(deltaMs)
    -> WaveSpawnerSystem.update()
    -> EnemyMovementSystem.update()
    -> TowerTargetingSystem.update()
    -> ProjectileSystem.update()
    -> EnemyDeathSystem.update()
    -> EndReachSystem.update()
    -> checkWinLoseConditions()
  -> emit GameSnapshot via StateFlow
    -> GameScreen recomposes
      -> GameCanvas draws snapshot
      -> HudOverlay shows stats
```

### VFX Trigger Mechanism
Domain Events (engine/) -> GameViewModel listens -> Triggers corresponding VFX:
```
GameEvent.EnemyKilled     -> ParticlePresets.explosion(position, enemyType.color)
GameEvent.TowerPlaced     -> ParticlePresets.smoke(position)
GameEvent.TowerUpgraded   -> ParticlePresets.sparkle(position)
GameEvent.LivesLost       -> ScreenShake.trigger(intensity=0.5)
GameEvent.WaveCompleted   -> FloatingText("Wave X Complete!", center)
GameEvent.GameWon         -> ParticlePresets.fireworks(screenCenter)
```

---

## Testing Strategy

### Unit Tests (JVM) --- Most Important
- `engine/` is entirely pure Kotlin, runs on JVM
- Each System gets independent tests
- ActionProcessor validation logic tests
- GameEngine integration tests

### ViewModel Tests (JVM + Turbine)
- State transition tests
- User action -> state change

### Instrumented Tests
- Room DAO tests
- Navigation tests
- GameScreen smoke tests

### TDD Rollback Flow
```
Write test (Red) -> Run test to confirm failure
  -> Write implementation (Green) -> Run test
    -> Pass -> Refactor -> Run test to confirm still passing
    -> Fail -> Analyze cause -> Fix implementation -> Re-run test (back to this step)
```

---

## Architecture Constraints

- `engine/` has **ZERO** imports from `android.*` or `androidx.*`
- `feature/*/ui/` does **NOT** import from `data/` directly (uses Hilt DI)
- No cross-feature imports
- Room entities and domain models are **SEPARATE** types with explicit mappers
- ViewModels expose `StateFlow`, **NEVER** `LiveData` or mutable state directly
- Screen orientation locked to portrait

---

## Feedback Loop Rules

1. **TDD discovers domain gap** -> return to domain model, update, regenerate affected test specs
2. **Architecture violation** -> return to TDD phase, refactor under green tests
3. **Test failure** -> analyze cause -> fix -> re-run (NEVER proceed with red tests)
4. **Any phase can trigger glossary update** -> all downstream artifacts must be re-validated

---

## Tech Stack
- **Compose BOM**: 2025.12.00
- **Hilt**: 2.59.2
- **Room**: 2.7.1
- **DataStore Preferences**: 1.1.7
- **Navigation Compose**: 2.9.0
- **JUnit5 + MockK + Turbine**: Testing
- **SoundPool**: Audio
- **Kotlin Coroutines**: Async

## Verification
1. Run all tests at the end of each Phase
2. Manual playtest on emulator after Phase 5
3. All 3 levels completable
4. Test on API 24 emulator for compatibility
5. No ANR, smooth 60fps
