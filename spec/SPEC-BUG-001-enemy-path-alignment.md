# SPEC-BUG-001: Enemy Path Alignment + Direction Indicator

**Status**: READY
**Branch**: `fix/bug-001-path-alignment`
**Priority**: CRITICAL (blocks gameplay)
**Depends on**: SPEC-003 (Game Systems)

---

## Problem

1. Enemies walk on a path that doesn't visually match the map grid's PATH cells
2. Player cannot tell where enemies enter the map (no entry/exit indicators)

## Root Cause Analysis

- `Levels.kt` defines `pathWaypoints` as grid coordinates (e.g., `GridPoint(0,2)`)
- `EnemyMovementSystem` converts waypoints to pixel positions using `cellSize`
- Potential issues:
  - Waypoints may not trace through every PATH cell (shortcuts between waypoints)
  - Pixel interpolation between sparse waypoints cuts through BUILDABLE/BLOCKED cells
  - Enemy sprite position may not center on the path tiles

---

## Requirements

### R1: Path Waypoints Must Trace All PATH Cells
- Every PATH cell in the grid must be visited by the enemy movement path
- Waypoints should include all turning points so enemies walk along PATH cells only
- Validate: draw debug line on waypoints; it must overlay PATH cells exactly

### R2: Entry/Exit Markers
- First PATH cell (entry point): show a green arrow indicating enemy spawn direction
- Last PATH cell (exit point): show a red X or skull icon
- Arrows rendered on the map layer (MapRenderer)
- Helps player understand enemy flow at a glance

### R3: Enemy Position Centering
- Enemy pixel position must be centered on PATH cell midpoints
- Interpolation between waypoints must follow cell centers, not diagonal shortcuts

---

## DDD Analysis

### Domain Changes
- `Levels.kt`: Fix all 3 levels' pathWaypoints to trace through every PATH cell center
- `LevelDefinition`: Add `entryPoint: GridPoint` and `exitPoint: GridPoint` (derived from first/last waypoint)
- `EnemyMovementSystem`: Ensure interpolation follows cell-center-to-cell-center path

### Validation
- `LevelDefinitionTest`: Add test verifying every waypoint is on a PATH cell
- `LevelDefinitionTest`: Add test verifying consecutive waypoints are adjacent or same-row/same-col (no diagonals)

---

## TDD Specs

### Unit Tests
1. Every pathWaypoint in Level 1/2/3 falls on a PATH cell
2. Consecutive waypoints share either row or col (no diagonal movement)
3. All PATH cells are reachable by walking the waypoint sequence
4. Enemy at pathProgress=0.0 is at the center of the first waypoint cell
5. Enemy at pathProgress=1.0 is at the center of the last waypoint cell
6. Enemy interpolation between two same-row waypoints stays on that row

### BDD Specs
```
Scenario: Enemy follows PATH cells exactly
  Given Level 1 map with S-shaped path
  When an enemy spawns and walks the full path
  Then the enemy position overlaps PATH cells at all times
  And never overlaps BUILDABLE or BLOCKED cells

Scenario: Entry and exit markers are visible
  Given Level 1 map is rendered
  When the player looks at the map
  Then a green arrow shows at the path entry
  And a red marker shows at the path exit
```

---

## Clean Architecture

### Engine Layer (engine/)
- `level/Levels.kt`: Fix pathWaypoints for all 3 levels
- `system/EnemyMovementSystem.kt`: Verify cell-center interpolation

### Presentation Layer (feature/game/)
- `renderer/MapRenderer.kt`: Draw entry arrow + exit marker

### Tests
- `level/LevelDefinitionTest.kt`: Path validation tests

---

## Acceptance Criteria
- [ ] Enemies visually walk on PATH cells only
- [ ] No diagonal shortcuts through non-path cells
- [ ] Entry point has green directional arrow
- [ ] Exit point has red marker
- [ ] All 3 levels' waypoints validated by tests
- [ ] All existing tests still pass
