# PixelFort - Tower Defense

Pixel-art tower defense game for Android, built with Kotlin + Jetpack Compose.

## Features

- **8 Tower Types**: Archer, Cannon, Magic, Sniper, Frost, Lightning, Poison, Bomb — each with unique targeting AI and visual effects
- **5 Enemy Types**: Goblin, Orc, Dragon, Troll, Specter — varied HP, speed, and reward
- **3 Campaign Levels**: Green Meadow, Desert Ruins, Frozen Fortress — each with unique map layout and wave composition
- **Meta-Progression System**: 13 persistent upgrades across 4 categories (Combat, Defense, Economy, Tower Unlocks) — spend Research Points earned from level completions
- **Tower Effects**: AoE splash, slow, poison DoT, chain lightning, AoE freeze
- **Status Effects**: Slowed, Poisoned — applied to enemies with visual indicators
- **Pixel-Art Rendering**: All towers, enemies, and projectiles drawn as grid-based pixel sprites via Compose Canvas
- **VFX Particle System**: Death bursts, hit effects, smoke, sparkles — up to 300 concurrent particles
- **Adaptive Mobile UI**: Dynamic cell sizing via BoxWithConstraints, scrollable tower HUD, tower tooltips
- **Star Rating**: 1-3 stars per level based on lives remaining

## Tech Stack

- **Language**: Kotlin 2.2.10
- **UI**: Jetpack Compose (BOM 2025.12.00), Material 3
- **DI**: Hilt 2.59.2
- **Database**: Room 2.7.1
- **Settings**: DataStore Preferences 1.1.7
- **Navigation**: Navigation Compose 2.9.0
- **Testing**: JUnit 5, MockK, Turbine
- **Build**: AGP 9.1.0, Version Catalog, KSP

## Architecture

```
engine/          Pure Kotlin game domain (zero Android imports)
feature/         Presentation layer (ViewModels + Compose UI)
core/            Infrastructure (Room, DataStore, DI modules)
navigation/      Compose Navigation routing
```

- **DDD**: GameEngine as Aggregate Root; Tower/Enemy/Projectile as Entities; GridPoint/TowerStats as Value Objects; GameEvent as Domain Events
- **TDD**: Red-Green-Refactor cycle with 16+ test files
- **BDD**: Given/When/Then scenario naming in system tests
- **Clean Architecture**: Strict layer separation — engine has zero Android dependencies

## Building

```bash
./gradlew assembleDebug
```

## Testing

```bash
./gradlew test
```

## License

All rights reserved.
