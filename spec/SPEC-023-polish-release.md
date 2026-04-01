# SPEC-023: Polish and Release

**Status**: READY
**Branch**: `feature/023-polish-release`
**Priority**: High
**Depends on**: All previous specs

---

## Requirements

### R1: Loading Screen
- Pixel-art animated loading screen
- PixelFort logo with shimmer animation
- Shown during app startup and level loading

### R2: App Icon
- Pixel-art app icon featuring a tower and shield
- Adaptive icon (foreground + background layers)
- All mipmap densities (mdpi through xxxhdpi)

### R3: UI Polish
- Screen transitions: fade or slide animations between nav destinations
- Button press haptic feedback (light vibration)
- Number rolling animation for gold/lives changes in HUD
- Lives lost: brief red vignette flash
- Toast-style notifications for "Tower placed!" etc.

### R4: Performance
- Verify 60fps on mid-range devices (API 28+)
- Profile with Android Studio profiler
- Optimize particle system if > 16ms frame time
- R8 minification enabled for release builds

### R5: Play Store Preparation
- App signing configured
- Privacy policy URL
- Feature graphic (1024x500 pixel art)
- Screenshots (phone + tablet)
- Store description (Chinese + English)
- Content rating questionnaire
- Target API level compliance (API 35)
- ProGuard rules for Room, Hilt, Navigation

### R6: Accessibility
- Content descriptions for interactive elements
- Sufficient color contrast ratios
- Support for system font scaling

---

## DDD Analysis

### Domain Changes
- None. All polish is presentation and build configuration

---

## TDD Specs

### Unit Tests
1. Rolling number animation interpolates correctly over duration
2. Screen transition composable renders without crash
3. All navigation routes resolve to real screens (no placeholders)

### Integration Tests
1. App launches without crash on API 24 emulator
2. All 5 levels load and render correctly
3. Settings persist across app restart
4. No memory leak after 10 game sessions

---

## Clean Architecture

### Build Config
- `app/build.gradle.kts`: R8 config, signing, ProGuard rules
- `proguard-rules.pro`: keep rules for Hilt, Room, serialization

### Presentation Layer
- `core/ui/LoadingScreen.kt`: animated loading
- `core/ui/ScreenTransitions.kt`: nav transition animations
- `feature/game/ui/GameScreen.kt`: vignette flash, haptic feedback
- `feature/game/ui/HudOverlay.kt`: rolling numbers

### Assets
- `res/mipmap-*/ic_launcher*.webp`: app icon
- `res/drawable/feature_graphic.webp`: store graphic

---

## Acceptance Criteria
- [ ] Loading screen shows on app start
- [ ] App icon is pixel-art style, all densities
- [ ] Screen transitions are smooth
- [ ] 60fps maintained on mid-range device
- [ ] R8 minification enabled in release build
- [ ] No crashes on API 24-35 range
- [ ] Play Store listing assets ready
- [ ] Accessibility audit passes basic checks
- [ ] App is uploadable to Play Console
