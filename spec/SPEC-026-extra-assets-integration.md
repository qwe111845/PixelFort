# SPEC-026: Extra Assets Integration (Icon, Backgrounds, Result Screens)

**Status**: READY
**Branch**: `feature/026-extra-assets`
**Priority**: Medium
**Depends on**: SPEC-024 (Sprite Art Integration)

---

## Requirements

### R1: App Icon
- Use generated `app_icon.png` (1024×1024) as Android app icon
- Generate proper mipmap sizes: mdpi(48), hdpi(72), xhdpi(96), xxhdpi(144), xxxhdpi(192)
- Replace default `ic_launcher.webp` and `ic_launcher_round.webp`

### R2: Main Menu Background
- Load `menu_background.png` in `MainMenuScreen`
- Display as full-screen background behind menu buttons
- Apply slight dark overlay so text remains readable

### R3: Victory/Defeat Illustrations
- Load `result_victory.png` in `GameOverOverlay` when game is won
- Load `result_defeat.png` in `GameOverOverlay` when game is lost
- Scale to fit width with aspect ratio maintained
- Display above the result text and buttons

## Files to Modify
| File | Change |
|------|--------|
| `res/mipmap-*/ic_launcher*.webp` | Replace with generated icon |
| `feature/menu/ui/MainMenuScreen.kt` | Add background image |
| `feature/game/ui/GameScreen.kt` (GameOverOverlay section) | Add victory/defeat illustration |

## Acceptance Criteria
- [ ] App icon shows custom PixelFort design in launcher
- [ ] Main menu has background illustration
- [ ] Victory screen shows celebration image
- [ ] Defeat screen shows defeat image
- [ ] Images scale properly on different screen sizes
