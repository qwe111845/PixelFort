# SPEC-021: Advanced VFX (Trails, Animations, Environment)

**Status**: READY
**Branch**: `feature/021-advanced-vfx`
**Priority**: Medium
**Depends on**: SPEC-010 (VFX System), SPEC-013 (Floating Text)

---

## Requirements

### R1: Projectile Trails
- Each projectile leaves a fading trail of 5-8 previous positions
- Trail color matches projectile color (per TowerEffect type)
- Trail segments shrink in size toward the tail
- Trail alpha fades: newest = 0.8, oldest = 0.1

### R2: Tower Idle Animation
- Towers have subtle idle animations (breathing/bobbing)
- 2-3 pixel vertical oscillation at ~0.5Hz
- Different idle patterns per tower type:
  - ARCHER: bow string vibrate
  - MAGIC: orb glow pulse (alpha oscillation)
  - LIGHTNING: spark crackle (random small particles)
  - POISON: bubble float (occasional small green circles)

### R3: Enemy Walk Animation
- Enemies have 2-frame walk cycle (alternate legs/body shift)
- Animation speed tied to enemy movement speed
- Dead enemies: brief white flash (60ms) before death burst particles

### R4: Environmental Ambiance
- Grass cells: occasional blade sway (subtle pixel shift)
- Path cells: dust particles when enemies walk over them
- Ambient particles: fireflies on Level 1, sand wisps on Level 2, snowflakes on Level 3

---

## DDD Analysis

### Domain Changes
- None. All purely presentation layer animations
- Trail positions computed from projectile movement history (ViewModel tracks)

### Presentation Objects
- `TrailPoint(x, y, alpha, size)` per projectile
- `AnimationClock`: global frame counter for synchronized animations
- `AmbientParticleConfig` per level theme

---

## TDD Specs

### Unit Tests
1. Trail stores last 8 positions of a projectile
2. Trail alpha decays correctly (newest=0.8, oldest=0.1)
3. AnimationClock ticks correctly with deltaMs
4. Idle bob offset returns correct Y for given time (sin wave)
5. Walk animation frame alternates based on pathProgress delta

### BDD Specs
```
Scenario: Projectile leaves trail
  Given a projectile is moving from (100,100) to (200,200)
  When the projectile moves 5 frames
  Then a trail of 5 fading dots appears behind it

Scenario: Tower bobs idle
  Given an archer tower with no enemies in range
  When 2 seconds pass
  Then the tower sprite oscillates vertically by 2 pixels
```

---

## Clean Architecture

### Presentation Layer (feature/game/)
- `vfx/ProjectileTrail.kt`: trail point tracking
- `vfx/AnimationClock.kt`: frame-synced timer
- `vfx/AmbientSystem.kt`: level-themed ambient particles
- `renderer/ProjectileRenderer.kt`: draw trail behind projectile
- `renderer/TowerRenderer.kt`: apply idle animation offset
- `renderer/EnemyRenderer.kt`: 2-frame walk cycle + death flash
- `renderer/MapRenderer.kt`: grass sway, ambient particles

---

## Acceptance Criteria
- [ ] Projectiles leave visible fading trails
- [ ] Towers gently bob when idle
- [ ] Enemies have 2-frame walk animation
- [ ] Death flash appears before death particles
- [ ] Each level has unique ambient particles
- [ ] Animations do not cause frame drops (< 300 draw calls)
- [ ] All existing tests still pass
