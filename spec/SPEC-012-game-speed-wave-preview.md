# SPEC-012: Game Speed Control + Wave Preview

**Status**: READY
**Branch**: `feature/012-speed-wave-preview`
**Priority**: High
**Depends on**: SPEC-006 (HUD), SPEC-011 (Adaptive UI)

---

## Requirements

### R1: Game Speed Control
- Player can toggle game speed: 1x / 2x / 3x
- Speed button displayed in TopInfoBar (next to pause)
- Speed multiplier applies to `deltaMs` passed to `GameEngine.update()`
- Speed resets to 1x when wave ends or game pauses
- Visual indicator shows current speed (e.g. "▶▶" for 2x, "▶▶▶" for 3x)

### R2: Wave Preview
- Before each wave starts (WaitingForWave state), show upcoming enemy composition
- Display: enemy type icon + count (e.g. "Goblin x6, Orc x2")
- Preview panel appears above the "Start Wave" button in HUD
- Uses data from `Levels.getById(levelId).waves[currentWave]`

---

## DDD Analysis

### Domain Changes
- `GameEngine`: Add `speedMultiplier: Float` field, apply to `update(deltaMs * speedMultiplier)`
- `GameSnapshot`: Add `speedMultiplier: Float` field
- `GameAction`: Add `SetSpeed(multiplier: Float)` variant

### No new Aggregates or Entities needed

---

## TDD Specs

### Unit Tests
1. `GameEngine.update()` with speedMultiplier=2.0 should advance game at double rate
2. `GameEngine.processAction(SetSpeed(3.0))` sets multiplier, snapshot reflects it
3. Speed resets to 1.0 on `GameState.WaitingForWave` transition
4. Speed resets to 1.0 on `pause()`
5. Wave preview data extraction: given level 1 wave 3, extract enemy groups correctly

### BDD Specs
```
Scenario: Player toggles speed to 2x
  Given the game is Playing at 1x speed
  When the player taps the speed button
  Then the speed changes to 2x
  And the speed indicator shows "▶▶"

Scenario: Wave preview shows next wave composition
  Given the game state is WaitingForWave at wave 2
  When the HUD renders
  Then the wave preview shows "哥布林 x6, 獸人 x2"
```

---

## Clean Architecture

### Engine Layer (engine/)
- `GameEngine.kt`: speedMultiplier field + SetSpeed action
- `GameAction.kt`: add SetSpeed variant
- `GameSnapshot.kt`: add speedMultiplier field

### Presentation Layer (feature/game/)
- `GameViewModel.kt`: expose setSpeed(Float), pass to engine
- `GameUiState.kt`: add speedMultiplier field
- `GameScreen.kt` TopInfoBar: speed toggle button
- `HudOverlay.kt`: wave preview panel

---

## Acceptance Criteria
- [ ] Speed button cycles 1x -> 2x -> 3x -> 1x
- [ ] Game visibly runs faster at 2x and 3x
- [ ] Speed resets on pause and wave end
- [ ] Wave preview shows correct enemy types and counts before each wave
- [ ] All existing tests still pass
