# SPEC-013: Floating Text + Damage Numbers

**Status**: READY
**Branch**: `feature/013-floating-text`
**Priority**: High
**Depends on**: SPEC-010 (VFX System)

---

## Requirements

### R1: Floating Damage Numbers
- When a projectile hits an enemy, display the damage amount as floating text
- Text floats upward and fades out over ~800ms
- Color matches the tower effect type:
  - Normal: White
  - AoE: Orange
  - Slow: Cyan
  - Poison tick: Green
  - Chain: Yellow
- Critical hits (if tower damage > enemy remaining HP) show larger text

### R2: Floating Gold Text
- When an enemy dies, display "+Xg" floating upward from death position
- Gold color (#FFD700), slightly larger than damage text
- Duration ~1000ms

### R3: Status Text
- "Wave X Complete!" centered on screen, large text, fades over 2s
- "-1 Life!" red flash text when enemy reaches end
- "+X RP" on victory screen (already partially exists)

---

## DDD Analysis

### Domain Changes
- `GameEvent.ProjectileHit`: already has pixelX, pixelY, effect --- sufficient
- `GameEvent.EnemyKilled`: already has pixelX, pixelY, reward --- sufficient
- No engine changes needed, purely presentation layer

### New Value Object
- `FloatingText(x, y, text, color, fontSize, lifeMs, maxLifeMs, vy)`

---

## TDD Specs

### Unit Tests
1. FloatingText constructor calculates correct initial velocity
2. FloatingText.update(deltaMs) moves position upward and reduces lifeMs
3. FloatingText.alpha returns lifeMs/maxLifeMs ratio
4. FloatingText.isDead returns true when lifeMs <= 0
5. FloatingTextSystem.processEvents creates correct texts for ProjectileHit
6. FloatingTextSystem.processEvents creates gold text for EnemyKilled
7. FloatingTextSystem.update removes dead texts

### BDD Specs
```
Scenario: Damage number appears on hit
  Given a projectile hits an enemy for 25 damage
  When the hit event is processed
  Then a floating "25" appears at the hit position
  And it floats upward and fades out

Scenario: Gold text appears on kill
  Given an enemy worth 10g is killed
  When the death event is processed
  Then a floating "+10g" appears in gold color
```

---

## Clean Architecture

### Presentation Layer (feature/game/)
- `vfx/FloatingText.kt`: data class for floating text
- `vfx/FloatingTextSystem.kt`: manages active texts, processes events
- `renderer/FloatingTextRenderer.kt`: DrawScope extension to draw texts
- `GameCanvas.kt`: add FloatingTextRenderer layer
- `GameViewModel.kt`: integrate FloatingTextSystem alongside ParticleSystem
- `GameUiState.kt`: add floatingTexts list

---

## Acceptance Criteria
- [ ] Damage numbers appear at projectile hit positions
- [ ] Damage color matches tower effect type
- [ ] "+Xg" gold text appears on enemy death
- [ ] "Wave X Complete!" shows between waves
- [ ] All text floats up and fades out smoothly
- [ ] No performance degradation with many simultaneous texts
- [ ] All existing tests still pass
