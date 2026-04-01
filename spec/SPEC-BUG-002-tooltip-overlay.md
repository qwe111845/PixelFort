# SPEC-BUG-002: Tower Tooltip as Floating Overlay

**Status**: READY
**Branch**: `fix/bug-002-tooltip-overlay`
**Priority**: High
**Depends on**: SPEC-011 (Adaptive UI)

---

## Problem

Current tower tooltip in HudOverlay renders inline, pushing other UI elements and affecting layout. User wants a non-intrusive floating tooltip that doesn't disrupt the HUD.

---

## Requirements

### R1: Floating Tooltip Overlay
- Tooltip appears as a floating popup OVER the game screen (not inline in HUD)
- Positioned near the tower button that triggered it, but never off-screen
- Semi-transparent dark background with rounded corners
- Does NOT push or resize any existing UI elements

### R2: Trigger Behavior
- **Long press** (~500ms hold) on a tower button in HUD: shows tooltip
- **Tap on info "ⓘ" button**: shows tooltip immediately
- **Normal tap** on tower button: selects tower for placement (existing behavior unchanged)
- Tooltip auto-dismisses after 4 seconds or on any tap elsewhere

### R3: Tooltip Content
- Tower name (Chinese)
- Tower description (Chinese)
- Stats: damage, range, fire rate, cost
- If tower is locked: show "🔒 需要場外升級解鎖"

---

## DDD Analysis

### Domain Changes
- None. Purely presentation layer

---

## TDD Specs

### Unit Tests
1. Tooltip state starts as null (not shown)
2. Setting tooltip tower type makes it non-null
3. Tooltip auto-dismiss timer resets on new tooltip
4. Dismissing tooltip sets state back to null

---

## Clean Architecture

### Presentation Layer (feature/game/)
- `ui/HudOverlay.kt`: Remove inline TowerTooltip, add long-press detection on tower buttons
- `ui/GameScreen.kt`: Add floating TowerTooltipOverlay composable positioned via Popup or Box overlay
- Tooltip state managed via `remember { mutableStateOf<TowerType?>(null) }`

---

## Acceptance Criteria
- [ ] Tooltip appears on long press or ⓘ tap
- [ ] Tooltip floats over the screen without affecting layout
- [ ] Normal tap still selects tower for placement
- [ ] Tooltip shows name, description, stats
- [ ] Tooltip auto-dismisses after 4 seconds
- [ ] Tapping elsewhere dismisses tooltip
- [ ] All existing tests still pass
