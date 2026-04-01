# SPEC-022: Content Balance + More Levels

**Status**: READY
**Branch**: `feature/022-content-balance`
**Priority**: High
**Depends on**: SPEC-015 (Boss), SPEC-016 (Difficulty)

---

## Requirements

### R1: Two New Levels
- **Level 4 "熔岩洞窟" (Lava Cavern)**: 10×14 grid, branching paths with merge, 8 waves
  - Theme: dark red/orange tiles, lava hazard cells (damage enemies passing through -10 HP)
  - New mechanic: lava cells are unbuildable but damage enemies
- **Level 5 "天空神殿" (Sky Temple)**: 12×16 grid, spiral with teleport shortcut, 10 waves
  - Theme: light blue/gold tiles, cloud aesthetic
  - New mechanic: teleport portal (enemy skips portion of path)
  - Final boss: enhanced BOSS_DRAGON with 2000 HP

### R2: Economy Balance Pass
- Review and adjust gold rewards so player can afford 4-6 towers by wave 3
- Ensure Hard mode is beatable with optimal play
- Tower upgrade costs scale to remain meaningful in late-game
- Meta upgrade costs balanced for ~15 hours total unlock time

### R3: Wave Composition Tuning
- Ensure each level introduces a new challenge:
  - Level 1: Learn basics (Goblin only, then Goblin+Orc)
  - Level 2: Mixed types, first Specter encounter
  - Level 3: All types, first Dragon
  - Level 4: Heavy Troll waves, boss
  - Level 5: Maximum variety, double boss, teleport path

### R4: Star Thresholds Review
- Verify 3-star thresholds are achievable but challenging
- Consider per-level custom thresholds (not just percentage-based)

---

## DDD Analysis

### Domain Changes
- `CellType`: Add `LAVA(isBuildable = false)` with damage property
- `GameMap`: Support teleport waypoints (skip path segments)
- `EnemyMovementSystem`: Handle lava damage per cell traversal
- `LevelDefinition`: Add optional `cellEffects: Map<GridPoint, CellEffect>`
- `CellEffect` sealed: `LavaDamage(dmg: Int)`, `Teleport(targetWaypoint: Int)`
- `Levels.kt`: Add Level 4 and Level 5

### Modified Systems
- `EnemyMovementSystem`: Check for cell effects during movement

---

## TDD Specs

### Unit Tests
1. Lava cell deals 10 damage to enemy passing through it
2. Teleport cell moves enemy to target waypoint instantly
3. Level 4 has 8 waves with boss in final wave
4. Level 5 has 10 waves with enhanced boss
5. Economy balance: Level 1 starting gold 200 affords 2 archers by wave 1
6. Star threshold: 80% lives = 3 stars on all levels

### BDD Specs
```
Scenario: Enemy takes lava damage
  Given an enemy walking through a lava cell
  When the enemy's position overlaps the lava cell
  Then the enemy takes 10 damage

Scenario: Teleport portal
  Given an enemy reaching the teleport entry point
  When the enemy enters the portal
  Then the enemy appears at the teleport exit waypoint
```

---

## Clean Architecture

### Engine Layer (engine/)
- `model/CellEffect.kt`: sealed interface for cell effects
- `model/GameMap.kt`: add cellEffects map
- `system/EnemyMovementSystem.kt`: apply cell effects
- `level/Levels.kt`: Level 4, Level 5 definitions

### Presentation Layer (feature/)
- `renderer/MapRenderer.kt`: lava/portal tile rendering
- `levelselect/LevelSelectScreen.kt`: show 5 levels

---

## Acceptance Criteria
- [ ] Level 4 and Level 5 playable with unique mechanics
- [ ] Lava damages enemies passing through
- [ ] Teleport works correctly
- [ ] Economy balanced across all 5 levels and 3 difficulties
- [ ] Wave composition creates meaningful progression
- [ ] Star thresholds reviewed and tuned
- [ ] All existing tests still pass
