# SPEC-018: Settings Screen

**Status**: READY
**Branch**: `feature/018-settings`
**Priority**: Medium
**Depends on**: SPEC-007 (Navigation)

---

## Requirements

### R1: Settings Categories

#### Audio
- Master Volume slider (0-100%)
- SFX Volume slider (0-100%)
- Music Volume slider (0-100%)

#### Gameplay
- Screen Shake toggle (on/off, default: on)
- Damage Numbers toggle (on/off, default: on)
- Show FPS Counter toggle (on/off, default: off)

#### Display
- Language selector (placeholder for future i18n)

#### About
- Version number
- Credits
- Open-source licenses

### R2: DataStore Persistence
- All settings persist via Preferences DataStore
- Settings apply immediately (no "Save" button)
- ViewModel exposes settings as StateFlow

---

## DDD Analysis

### Domain Changes
- None. Settings is purely infrastructure + presentation

### Infrastructure
- `SettingsPreferences` data class with all fields
- `SettingsDataStore` reads/writes to DataStore

---

## TDD Specs

### Unit Tests
1. SettingsDataStore saves and loads masterVolume correctly
2. SettingsDataStore saves and loads screenShakeEnabled correctly
3. Default values are correct when DataStore is empty
4. SettingsViewModel exposes current values via StateFlow

### BDD Specs
```
Scenario: Toggle screen shake off
  Given screen shake is enabled (default)
  When the player toggles the screen shake switch off
  Then screen shake is disabled
  And the setting persists after app restart
```

---

## Clean Architecture

### Infrastructure Layer (core/)
- `datastore/SettingsDataStore.kt`: DataStore<Preferences> wrapper
- `datastore/SettingsPreferences.kt`: data class for all settings
- `di/DataStoreModule.kt`: Hilt provider

### Presentation Layer (feature/)
- `settings/SettingsViewModel.kt`: @HiltViewModel
- `settings/ui/SettingsScreen.kt`: Compose UI with sliders and toggles

### Navigation
- `Routes.Settings` already exists
- `AppNavGraph`: replace placeholder with real SettingsScreen

---

## Acceptance Criteria
- [ ] Settings screen accessible from main menu
- [ ] Volume sliders control audio (when audio exists)
- [ ] Screen shake toggle works in-game
- [ ] Damage numbers toggle works in-game
- [ ] FPS counter toggle shows/hides counter
- [ ] All settings persist via DataStore
- [ ] All existing tests still pass
