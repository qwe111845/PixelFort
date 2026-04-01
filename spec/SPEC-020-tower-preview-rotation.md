# SPEC-020: Tower Placement Preview + Turret Rotation

**Status**: READY
**Branch**: `feature/020-tower-preview-rotation`
**Priority**: Medium
**Depends on**: SPEC-005 (UI Rendering), SPEC-011 (Adaptive UI)

---

## Requirements

### R1: Tower Placement Ghost Preview
- When a tower type is selected in HUD, show a semi-transparent "ghost" tower at the player's finger position
- Ghost follows finger drag across the grid
- Ghost snaps to grid cells
- Ghost shows range circle (dashed, semi-transparent)
- Ghost turns red if cell is unbuildable or occupied
- On tap/release on valid cell, tower is placed

### R2: Turret Rotation
- Tower sprites visually rotate to face their current target
- Rotation is smooth (lerp toward target angle at ~10 rad/s)
- When no target: tower faces last target direction (idle)
- Rotation applies to the top portion of the tower sprite (e.g., cannon barrel, arrow tip)

### R3: Tower Count Badge
- Each tower button in HUD shows a small badge with count of that tower type placed
- Badge positioned at top-right corner of tower button
- E.g., "2" if 2 archers are placed

---

## DDD Analysis

### Domain Changes
- `Tower`: add `facingAngle: Float` field (radians, default 0)
- `TowerTargetingSystem`: compute angle to target, update tower.facingAngle
- `GameSnapshot.towers`: already includes all tower data

### Presentation Changes
- Ghost preview is purely UI (GameCanvas + pointer input)
- Tower count is computed from snapshot.towers.groupBy { it.type }

---

## TDD Specs

### Unit Tests
1. TowerTargetingSystem computes correct facingAngle toward target enemy
2. facingAngle lerps smoothly (not instant snap)
3. facingAngle retains last value when no target in range
4. Ghost preview position snaps to grid: pixel (135, 240) with cellSize 80 -> grid (1, 3)

### BDD Specs
```
Scenario: Ghost tower follows finger
  Given the player has selected Archer tower
  When the player drags finger over cell (3, 4)
  Then a semi-transparent archer appears at (3, 4)
  And a range circle is shown

Scenario: Turret rotates toward enemy
  Given an archer tower at (3, 4) facing north
  When an enemy enters range from the east
  Then the tower smoothly rotates to face east
```

---

## Clean Architecture

### Engine Layer (engine/)
- `model/Tower.kt`: add facingAngle field
- `system/TowerTargetingSystem.kt`: compute and update facingAngle

### Presentation Layer (feature/game/)
- `ui/GameCanvas.kt`: ghost tower rendering with pointer drag
- `renderer/TowerRenderer.kt`: apply rotation transform to sprite top
- `ui/HudOverlay.kt`: tower count badge on buttons

---

## Acceptance Criteria
- [ ] Ghost tower appears when tower type selected and finger on canvas
- [ ] Ghost shows range ring and turns red on invalid cells
- [ ] Towers visually rotate to face their targets
- [ ] Rotation is smooth, not instant
- [ ] Tower count badges show on HUD buttons
- [ ] All existing tests still pass
