# SPEC-016: Difficulty Modes + Endless Mode

**Status**: READY
**Branch**: `feature/016-difficulty-endless`
**Priority**: Medium
**Depends on**: SPEC-004 (GameEngine), SPEC-009 (Meta Progression)

---

## Requirements

### R1: Difficulty Modes
- Three modes: Easy / Normal / Hard
- Difficulty modifiers applied at game start:

| Modifier | Easy | Normal | Hard |
|----------|------|--------|------|
| Enemy HP | 0.75x | 1.0x | 1.5x |
| Enemy Speed | 0.9x | 1.0x | 1.15x |
| Starting Gold | +100 | +0 | -50 |
| Starting Lives | +10 | +0 | -5 |
| Gold Reward | 1.2x | 1.0x | 0.8x |
| RP Multiplier | 0.5x | 1.0x | 2.0x |

- Difficulty selected on LevelSelectScreen before starting level
- Stars earned on Hard give bonus RP
- Save best stars per difficulty per level

### R2: Endless Mode
- Unlocked after completing all 3 levels on Normal
- Uses Level 1 map (or random map rotation)
- Waves auto-generate with scaling difficulty:
  - Wave N: enemy HP *= 1 + (N * 0.15), count += N/3
  - New enemy types introduced every 5 waves
  - Boss every 10 waves
- No victory condition --- play until all lives lost
- Track high score: waves survived + total kills
- Leaderboard stored locally (Room)

---

## DDD Analysis

### Domain Changes
- `DifficultyMode` enum: EASY, NORMAL, HARD with modifier values
- `DifficultyModifier` data class: hpMul, speedMul, goldMul, rpMul, goldBonus, livesBonus
- `GameEngine` constructor: accept DifficultyModifier
- `EndlessWaveGenerator`: generates Wave objects procedurally
- `GameSnapshot`: add currentDifficulty, isEndless fields
- `LevelDefinition`: add variant for endless mode

### New Entities
- `EndlessHighScore(wavesReached, totalKills, date)`

---

## TDD Specs

### Unit Tests
1. DifficultyModifier.HARD applies 1.5x HP to spawned enemies
2. DifficultyModifier.EASY gives +100 starting gold
3. EndlessWaveGenerator.generate(waveN=10) produces boss wave
4. EndlessWaveGenerator.generate(waveN=20) has higher HP multiplier than wave 10
5. RP earned on Hard is 2x normal
6. High score save/load roundtrip via Room DAO

### BDD Specs
```
Scenario: Hard mode enemies have more HP
  Given the player starts Level 1 on Hard difficulty
  When a Goblin spawns
  Then its HP is 75 (50 * 1.5)

Scenario: Endless mode scales waves infinitely
  Given the player is in endless mode at wave 15
  When wave 15 spawns
  Then enemies have 1 + (15 * 0.15) = 3.25x HP
  And enemy count is increased
```

---

## Clean Architecture

### Engine Layer (engine/)
- `model/DifficultyMode.kt`: enum + modifier data class
- `GameEngine.kt`: apply difficulty modifiers to spawn and economy
- `level/EndlessWaveGenerator.kt`: procedural wave creation

### Data Layer (core/)
- `database/entity/EndlessHighScoreEntity.kt`
- `database/dao/EndlessHighScoreDao.kt`

### Presentation Layer (feature/)
- `levelselect/LevelSelectScreen.kt`: difficulty selector chips
- `endless/EndlessScreen.kt`: endless mode UI with wave counter
- `endless/EndlessViewModel.kt`: manages endless game state

---

## Acceptance Criteria
- [ ] Three difficulty modes selectable per level
- [ ] Difficulty modifiers visibly affect gameplay
- [ ] RP reward scales with difficulty
- [ ] Endless mode unlocks after clearing all 3 levels
- [ ] Waves scale infinitely with boss every 10 waves
- [ ] High score tracked and displayed
- [ ] All existing tests still pass
