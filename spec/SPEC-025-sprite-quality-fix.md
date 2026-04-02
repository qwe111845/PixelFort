# SPEC-025: Sprite Quality Fix (Background Removal + Consistency)

**Status**: READY
**Branch**: `feature/025-sprite-quality-fix`
**Priority**: High
**Depends on**: SPEC-024 (Sprite Art Integration)

---

## Requirements

### R1: Background Removal (去背)
- Install `rembg` Python package in ComfyUI venv
- Create batch script to remove white backgrounds from all 41 PNG sprites
- Output RGBA PNGs with transparent backgrounds
- Overwrite existing sprites in `assets/sprites/`

### R2: Enemy Sprite Consistency
- Current issue: frame 1 and frame 2 of same enemy look completely different (generated independently)
- Fix: Use single frame per enemy (remove walk animation frame toggle)
- In `EnemyRenderer.kt`: always use frame 1, remove `elapsedMs` frame switching
- Keep frame 2 files for future use but don't render them

### R3: Tower Sprite Rendering Fix
- Verify all 8 tower types × 3 levels render correctly
- Fix sprite scaling to properly fit within grid cell
- Ensure sprite doesn't overlap adjacent cells

### R4: Sprite Style Alignment
- Regenerate sprites with improved prompts matching user's preferred style:
  - Bold outlines, graphic design anime style (Limbus Company / Project Moon aesthetic)
  - Clean white/transparent background
  - High contrast, saturated colors
  - Clear silhouette recognizable at 60-80px

## Implementation Steps
1. Install rembg: `pip install rembg[gpu]`
2. Run background removal on all sprites
3. Update EnemyRenderer to single-frame mode
4. Verify tower rendering at all levels
5. Optionally regenerate sprites with refined style

## Files to Modify
| File | Change |
|------|--------|
| `feature/game/renderer/EnemyRenderer.kt` | Remove frame toggle, always use frame 1 |
| `feature/game/ui/GameCanvas.kt` | Remove `elapsedMs` from drawEnemies call |
| `assets/sprites/**/*.png` | Replace with transparent-background versions |

## Acceptance Criteria
- [ ] All sprites have transparent backgrounds (no white/colored bg)
- [ ] Each enemy always shows same consistent sprite
- [ ] All 24 tower sprites render correctly in-game
- [ ] Sprites are clear and recognizable at game cell size
