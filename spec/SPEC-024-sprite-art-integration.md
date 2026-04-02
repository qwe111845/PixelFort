# SPEC-024: Sprite Art Integration (PNG Assets)

**Status**: READY
**Branch**: `feature/024-sprite-art-integration`
**Priority**: High
**Depends on**: SPEC-005 (UI Rendering), SPEC-008 (8 Tower Types), SPEC-010 (VFX)

---

## Requirements

### R1: Sprite Asset Loader
- Create `SpriteAssetLoader` that loads PNG sprites from `assets/sprites/`
- Load all tower sprites (24 PNGs: 8 types × 3 levels) on game init
- Load all enemy sprites (13 PNGs: 5 types × 2 frames + boss × 3) on game init
- Cache decoded `ImageBitmap` in memory for zero-allocation rendering
- Provide API: `getTowerSprite(type: TowerType, level: Int): ImageBitmap`
- Provide API: `getEnemySprite(type: EnemyType, frame: Int): ImageBitmap`
- Provide API: `getBossEnrageSprite(): ImageBitmap`

### R2: Tower Rendering with PNG Sprites
- Replace procedural `drawArcher()`, `drawCannon()`, etc. in `TowerRenderer.kt`
- Use `DrawScope.drawImage()` with loaded `ImageBitmap`
- Scale sprite to fit cell size (maintain aspect ratio)
- Keep existing level-pip indicators below tower
- Keep existing range ring overlay for selected tower
- Keep existing cooldown visual feedback

### R3: Enemy Rendering with PNG Sprites
- Replace procedural `drawGoblin()`, `drawOrc()`, etc. in `EnemyRenderer.kt`
- Use `DrawScope.drawImage()` with loaded `ImageBitmap`
- Alternate between frame 1 and frame 2 based on `pathProgress` for walk animation
- Apply `enemy.type.size` scaling (Goblin 1.0x, Orc 1.3x, Dragon 1.6x, Boss 2.0x)
- Keep existing HP bar rendering above sprite
- Keep existing status effect overlays (slow tint, poison tint)
- Boss enrage: switch to `enemy_boss_dragon_enrage.png` when enraged

### R4: Extra Assets Integration
- App icon: `app_icon.png` → generate proper mipmap sizes for Android manifest
- Menu background: `menu_background.png` → display in `MainMenuScreen`
- Victory illustration: `result_victory.png` → display in `GameOverOverlay` on win
- Defeat illustration: `result_defeat.png` → display in `GameOverOverlay` on loss

### R5: Sprite Animation System
- Enemy walk cycle: toggle between frame 1 and frame 2 every 300ms
- Tower idle: subtle scale breathing animation (0.98x ↔ 1.02x, 1s cycle)
- Tower fire: brief flash/scale pulse on attack (1.0x → 1.1x → 1.0x, 150ms)

### R6: Fallback to Procedural
- If any sprite fails to load, fall back to existing procedural pixel-art drawing
- Log warning but do not crash the game

---

## Asset File Structure

```
app/src/main/assets/sprites/
├── towers/
│   ├── tower_archer_lv1.png    (512×512)
│   ├── tower_archer_lv2.png
│   ├── tower_archer_lv3.png
│   ├── tower_cannon_lv1.png
│   ├── ... (8 types × 3 levels = 24 files)
│   └── tower_bomb_lv3.png
├── enemies/
│   ├── enemy_goblin_1.png      (512×512, walk frame 1)
│   ├── enemy_goblin_2.png      (512×512, walk frame 2)
│   ├── enemy_orc_1.png
│   ├── enemy_orc_2.png
│   ├── enemy_dragon_1.png
│   ├── enemy_dragon_2.png
│   ├── enemy_troll_1.png
│   ├── enemy_troll_2.png
│   ├── enemy_specter_1.png
│   ├── enemy_specter_2.png
│   ├── enemy_boss_dragon_1.png
│   ├── enemy_boss_dragon_2.png
│   └── enemy_boss_dragon_enrage.png
└── extras/
    ├── app_icon.png            (1024×1024)
    ├── menu_background.png     (1920×1080)
    ├── result_victory.png      (1920×1080)
    └── result_defeat.png       (1920×1080)
```

---

## DDD Analysis

### Domain Changes
- **NONE** — sprite loading is a presentation concern, domain layer stays pure

### New Supporting Components
- `SpriteAssetLoader` (core/util/): loads and caches ImageBitmaps from assets
- `SpriteCache` (core/util/): holds decoded sprites in memory, keyed by type+level

### Modified Components
- `TowerRenderer.kt`: accept `SpriteCache`, use `drawImage()` instead of procedural
- `EnemyRenderer.kt`: accept `SpriteCache`, use `drawImage()` with frame alternation
- `GameCanvas.kt`: instantiate/pass `SpriteCache` to renderers
- `GameViewModel.kt` or `GameScreen.kt`: trigger sprite loading on init
- `MainMenuScreen.kt`: display menu background image
- `GameOverOverlay.kt`: display victory/defeat illustration

---

## TDD Specs

### Unit Tests

1. `SpriteAssetLoader` loads all 24 tower sprites without exception
2. `SpriteAssetLoader` loads all 13 enemy sprites without exception
3. `getTowerSprite(ARCHER, 1)` returns non-null ImageBitmap
4. `getTowerSprite(ARCHER, 1)` returns same instance on second call (cached)
5. `getEnemySprite(GOBLIN, 1)` returns non-null ImageBitmap
6. `getEnemySprite(GOBLIN, 1)` and `getEnemySprite(GOBLIN, 2)` return different bitmaps
7. `getBossEnrageSprite()` returns non-null ImageBitmap
8. Missing sprite file returns null and logs warning (no crash)
9. Walk animation frame alternates based on elapsed time (300ms toggle)
10. Tower scale animation oscillates between 0.98 and 1.02

### BDD Specs

```
Scenario: Tower renders with PNG sprite
  Given a level 2 archer tower placed at grid (3,4)
  And sprite cache has loaded tower_archer_lv2.png
  When the game canvas renders
  Then the archer sprite is drawn at the cell center
  And the sprite is scaled to fit the cell size
  And level pips still appear below the sprite

Scenario: Enemy walk animation alternates frames
  Given a goblin enemy moving along the path
  And sprite cache has enemy_goblin_1.png and enemy_goblin_2.png
  When 300ms elapses
  Then the rendered frame toggles from frame 1 to frame 2

Scenario: Boss shows enrage sprite
  Given a boss_dragon enemy with HP below 30%
  And the boss has Enraged status effect
  When the game canvas renders
  Then enemy_boss_dragon_enrage.png is drawn instead of normal sprite

Scenario: Missing sprite falls back to procedural
  Given tower_archer_lv1.png is missing from assets
  When the game canvas renders an archer tower at level 1
  Then the procedural pixel-art drawArcher() is used
  And a warning is logged

Scenario: Menu background displays
  Given the player is on the main menu screen
  When the screen renders
  Then menu_background.png fills the screen behind the menu buttons

Scenario: Victory screen shows illustration
  Given the player has won the game
  When the game over overlay appears
  Then result_victory.png is displayed prominently
```

---

## Clean Architecture

### Core Layer (core/util/)
```kotlin
// NEW: SpriteAssetLoader.kt
class SpriteAssetLoader(private val context: Context) {
    private val cache = mutableMapOf<String, ImageBitmap>()

    fun loadAll()           // preload all sprites
    fun getTowerSprite(type: TowerType, level: Int): ImageBitmap?
    fun getEnemySprite(type: EnemyType, frame: Int): ImageBitmap?
    fun getBossEnrageSprite(): ImageBitmap?
    fun getExtraAsset(name: String): ImageBitmap?
    fun dispose()           // release all bitmaps
}
```

### Feature Layer (feature/game/renderer/)
```kotlin
// MODIFIED: TowerRenderer.kt
fun DrawScope.drawTowers(
    towers: List<Tower>,
    cellSize: Float,
    selectedTowerId: Int?,
    spriteLoader: SpriteAssetLoader   // NEW parameter
)

// MODIFIED: EnemyRenderer.kt
fun DrawScope.drawEnemies(
    enemies: List<Enemy>,
    cellSize: Float,
    elapsedMs: Long,                   // NEW: for frame animation
    spriteLoader: SpriteAssetLoader    // NEW parameter
)
```

### DI (core/di/)
```kotlin
// MODIFIED: AppModule.kt — provide SpriteAssetLoader as singleton
@Provides @Singleton
fun provideSpriteAssetLoader(@ApplicationContext context: Context): SpriteAssetLoader
```

---

## Implementation Order

1. Create `SpriteAssetLoader` with tests (TDD)
2. Modify `TowerRenderer.kt` — sprite rendering + procedural fallback
3. Modify `EnemyRenderer.kt` — sprite rendering + walk frame toggle + fallback
4. Modify `GameCanvas.kt` — pass loader to renderers
5. Wire DI: provide loader via Hilt, inject into ViewModel
6. Integrate extra assets: menu background, victory/defeat screens
7. Add sprite animations (breathing, fire pulse)
8. Generate app icon mipmaps from `app_icon.png`

---

## Acceptance Criteria

- [ ] All 24 tower sprites render correctly at all 3 levels
- [ ] All 13 enemy sprites render with walk animation
- [ ] Boss switches to enrage sprite below 30% HP
- [ ] HP bar, status overlays, range ring still work over sprites
- [ ] Menu background, victory/defeat images display
- [ ] Missing sprite gracefully falls back to procedural art
- [ ] No memory leaks — sprites disposed on game exit
- [ ] 60fps maintained with all sprites loaded
- [ ] All unit tests pass
- [ ] All BDD scenarios pass
