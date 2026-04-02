# BUG-003: Status Bar Overlapping Game UI Buttons

**Status**: READY
**Branch**: `fix/bug-003-statusbar-overlap`
**Priority**: Critical
**Depends on**: SPEC-006 (HUD)

---

## Problem
On some devices, the top info bar (pause/speed buttons) is covered by the system status bar, making buttons untappable.

## Root Cause
`GameScreen` uses `fillMaxSize()` without accounting for system `WindowInsets` (status bar, navigation bar, notch/cutout).

## Fix
- Add `WindowInsets.systemBars` padding to the top-level `Column` in `GameScreen.kt`
- Use `Modifier.statusBarsPadding()` or `WindowInsets.statusBars.asPaddingValues()`
- Ensure `enableEdgeToEdge()` is called in `MainActivity` if not already

## Files to Modify
| File | Change |
|------|--------|
| `feature/game/ui/GameScreen.kt` | Add `statusBarsPadding()` to top Column |
| `MainActivity.kt` | Ensure `enableEdgeToEdge()` is called |

## TDD Spec
1. Top info bar is fully visible below system status bar on all device sizes
2. Pause button is tappable
3. Speed toggle button is tappable
4. Game canvas is not cut off at bottom

## Acceptance Criteria
- [ ] Pause/speed buttons fully visible and tappable on devices with notch
- [ ] No overlap with system status bar
- [ ] Game layout still properly fills available space
