# SPEC-019: Tutorial System

**Status**: READY
**Branch**: `feature/019-tutorial`
**Priority**: Medium
**Depends on**: SPEC-005 (UI), SPEC-011 (Adaptive UI)

---

## Requirements

### R1: First-Play Tutorial
- Triggered automatically on first game launch (tracked via DataStore)
- Step-by-step overlay guiding the player through Level 1:

| Step | Action | Highlight | Text |
|------|--------|-----------|------|
| 1 | Welcome | Full screen | "Welcome to PixelFort!" |
| 2 | Show map | Map area | "Enemies follow the path from top to bottom" |
| 3 | Select tower | HUD tower bar | "Tap a tower to select it for placement" |
| 4 | Place tower | Buildable cells | "Tap a green cell to place your tower" |
| 5 | Start wave | Start button | "Press to send the first wave of enemies" |
| 6 | Upgrade | Placed tower | "Tap a placed tower to upgrade or sell it" |
| 7 | Complete | Full screen | "Good luck, Commander!" |

### R2: Tutorial Overlay
- Semi-transparent dark overlay with "spotlight" hole on highlighted element
- Instruction text at top or bottom (avoid overlapping spotlight)
- "Next" button + "Skip Tutorial" button
- Tutorial pauses game progression

### R3: Replay Tutorial
- "Replay Tutorial" button in Settings screen
- Resets tutorial completion flag

---

## DDD Analysis

### Domain Changes
- None. Tutorial is purely presentation + DataStore flag

### Presentation Objects
- `TutorialStep` data class: text, highlightRect, position
- `TutorialState`: currentStep, isActive, isCompleted

---

## TDD Specs

### Unit Tests
1. TutorialState starts at step 0 when not completed
2. TutorialState.advance() increments step
3. TutorialState.skip() marks completed
4. Tutorial does not trigger if already completed (DataStore flag)
5. Reset tutorial clears DataStore flag

### BDD Specs
```
Scenario: First-time player sees tutorial
  Given the player has never played before
  When the player starts Level 1
  Then the tutorial overlay appears at step 1

Scenario: Player skips tutorial
  Given the tutorial is showing step 3
  When the player taps "Skip Tutorial"
  Then the tutorial closes
  And the tutorial is marked as completed
```

---

## Clean Architecture

### Infrastructure Layer (core/)
- `datastore/SettingsDataStore.kt`: add `tutorialCompleted: Boolean`

### Presentation Layer (feature/game/)
- `tutorial/TutorialStep.kt`: step definitions
- `tutorial/TutorialOverlay.kt`: Compose overlay with spotlight
- `GameScreen.kt`: conditionally show TutorialOverlay
- `GameViewModel.kt`: track tutorial state, pause game during tutorial

### Settings Integration
- `settings/SettingsScreen.kt`: "Replay Tutorial" button

---

## Acceptance Criteria
- [ ] Tutorial appears on first game launch
- [ ] Tutorial guides through all 7 steps
- [ ] Spotlight highlights correct UI element per step
- [ ] "Skip" button closes tutorial and marks completed
- [ ] Tutorial does not appear on subsequent launches
- [ ] "Replay Tutorial" in settings resets the flag
- [ ] Game is paused during tutorial
- [ ] All existing tests still pass
