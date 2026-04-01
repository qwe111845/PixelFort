# SPEC-017: Audio System

**Status**: READY
**Branch**: `feature/017-audio`
**Priority**: Medium
**Depends on**: SPEC-006 (HUD), SPEC-018 (Settings)

---

## Requirements

### R1: Sound Effects (SoundPool)
- Event-triggered sound effects:

| Event | Sound | Priority |
|-------|-------|----------|
| Tower placed | "thud" click | Medium |
| Tower fires (per type) | "twang" / "boom" / "zap" etc. | Low |
| Projectile hit | "impact" | Low |
| Enemy killed | "splat" / "crunch" | Medium |
| Enemy reaches end | "alarm" buzz | High |
| Wave start | "horn" fanfare | High |
| Wave complete | "chime" | High |
| Victory | "victory fanfare" | Max |
| Defeat | "defeat" sad tone | Max |
| Button tap | "click" | Low |
| Tower upgrade | "sparkle ding" | Medium |
| Boss warning | "deep rumble" | High |

### R2: Background Music (MediaPlayer)
- Looping chiptune-style BGM per screen:
  - Main menu: calm melody
  - In-game: tension building loop
  - Victory: triumphant short loop
- Music fades between screens (500ms crossfade)

### R3: Volume Control
- Master volume, SFX volume, Music volume (0-100%)
- Settings persist via DataStore
- Mute button in TopInfoBar (quick toggle)

---

## DDD Analysis

### Domain Changes
- None. Audio is purely infrastructure (core/) + presentation
- GameEvents already provide all trigger points needed

### Infrastructure
- `SoundManager`: SoundPool wrapper for SFX
- `MusicManager`: MediaPlayer wrapper for BGM
- `AudioSettings`: DataStore-backed preferences

---

## TDD Specs

### Unit Tests
1. SoundManager.play(SoundEffect.TOWER_FIRE_ARCHER) calls SoundPool.play with correct ID
2. SoundManager respects volume settings (0 = silent)
3. MusicManager.crossfadeTo(track) fades old and fades in new
4. AudioSettings.masterVolume persists across DataStore reads
5. Mute toggle sets all volumes to 0, unmute restores previous

### BDD Specs
```
Scenario: Tower placement plays sound
  Given SFX volume is 80%
  When the player places an archer tower
  Then the "thud" sound plays at 80% volume

Scenario: Mute quick toggle
  Given music and SFX are playing
  When the player taps the mute button
  Then all audio stops immediately
  And the mute icon shows muted state
```

---

## Clean Architecture

### Infrastructure Layer (core/)
- `audio/SoundEffect.kt`: enum of all SFX IDs
- `audio/SoundManager.kt`: SoundPool lifecycle, play(), setVolume()
- `audio/MusicTrack.kt`: enum of BGM tracks
- `audio/MusicManager.kt`: MediaPlayer lifecycle, crossfade
- `audio/AudioModule.kt`: Hilt @Module providing managers
- `datastore/AudioSettings.kt`: DataStore preferences for volumes

### Presentation Layer (feature/)
- `game/viewmodel/GameViewModel.kt`: inject SoundManager, play on events
- `game/ui/GameScreen.kt`: TopInfoBar mute button
- `settings/SettingsScreen.kt`: volume sliders

### Assets
- `app/src/main/res/raw/`: .ogg sound effect files
- `app/src/main/assets/music/`: .ogg BGM files

---

## Acceptance Criteria
- [ ] All listed sound effects play on corresponding events
- [ ] Background music loops per screen with crossfade
- [ ] Volume sliders in Settings work (Master/SFX/Music)
- [ ] Mute button in game toggles all audio
- [ ] Audio settings persist across app restarts
- [ ] No audio leak on Activity destroy
- [ ] All existing tests still pass
