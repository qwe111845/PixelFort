# SPEC-014: Screen Shake + Impact Juice

**Status**: READY
**Branch**: `feature/014-screen-shake`
**Priority**: Medium
**Depends on**: SPEC-010 (VFX System)

---

## Requirements

### R1: Screen Shake
- Canvas applies random offset translation during shake events
- Shake triggers:
  - Enemy reaches end (loses life): intensity 0.4, duration 300ms
  - Boss enemy killed: intensity 0.6, duration 400ms
  - Bomb tower AoE explosion: intensity 0.3, duration 200ms
- Shake decays exponentially: offset *= 0.85 per frame
- Shake can be disabled in Settings

### R2: Impact Flash
- Brief white flash overlay (50ms) on large AoE hits
- Red screen edge flash (200ms) when lives lost

### R3: Tower Attack Recoil
- Tower sprite briefly scales down (0.85x) when firing, then springs back
- Recoil duration: 100ms compress, 200ms decompress
- Adds visual feedback that tower is actively firing

---

## DDD Analysis

### Domain Changes
- None. All visual juice is purely presentation layer
- Uses existing `GameEvent.LivesLost`, `GameEvent.EnemyKilled`, `GameEvent.ProjectileHit`

### New Presentation Objects
- `ScreenShake(intensityX, intensityY, remainingMs, decayRate)`
- `TowerRecoilState(towerId, phase, progress)` tracked per tower in ViewModel

---

## TDD Specs

### Unit Tests
1. ScreenShake.update(deltaMs) reduces remainingMs and decays intensity
2. ScreenShake.offset returns (0,0) when remainingMs <= 0
3. ScreenShake.offset returns randomized offset within intensity bounds
4. ScreenShake stacking: new shake replaces if stronger, ignores if weaker
5. TowerRecoilState progresses through compress -> decompress -> idle

### BDD Specs
```
Scenario: Screen shakes when life is lost
  Given the game is playing
  When an enemy reaches the end
  Then the canvas shakes for 300ms
  And the shake intensity decays over time

Scenario: Tower visually recoils when firing
  Given an archer tower fires a projectile
  When the TowerPlaced event is emitted
  Then the tower sprite briefly compresses and springs back
```

---

## Clean Architecture

### Presentation Layer (feature/game/)
- `vfx/ScreenShake.kt`: shake state and update logic
- `GameCanvas.kt`: apply Canvas.translate(shake.offsetX, shake.offsetY)
- `renderer/TowerRenderer.kt`: apply scale transform for recoil
- `GameViewModel.kt`: process events to trigger shakes and recoils
- `GameScreen.kt`: flash overlay composable

---

## Acceptance Criteria
- [ ] Screen shakes when lives are lost
- [ ] Bomb tower explosions cause mild shake
- [ ] Boss kills cause strong shake
- [ ] Towers visually recoil when firing
- [ ] Flash effects on large impacts
- [ ] Shake can be toggled off in settings
- [ ] All existing tests still pass
