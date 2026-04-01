# SPEC-015: Boss Mechanics

**Status**: READY
**Branch**: `feature/015-boss-mechanics`
**Priority**: High
**Depends on**: SPEC-003 (Game Systems), SPEC-010 (VFX)

---

## Requirements

### R1: Boss Enemy Type
- New `EnemyType.BOSS_DRAGON` with elevated stats:
  - HP: 1200, Speed: 1.0, Reward: 150g, Size: 2.0x
- Boss appears as final enemy in last wave of each level
- Boss has a unique pixel-art sprite (larger, more detailed)

### R2: Boss Armor System
- Boss has `armor: Int` that reduces incoming damage by flat amount
- armor = 30 means each hit deals `max(1, damage - 30)` damage
- Poison effect bypasses armor (true damage)
- Armor displayed as gray segment on HP bar

### R3: Boss Enrage Phase
- When boss HP drops below 30%, it enters "Enrage" state
- Enrage effect: speed increases by 50%, visual turns red-tinted
- Enrage is a new `StatusEffect.Enraged(speedBoost: Float)`

### R4: Boss Entry Warning
- 3 seconds before boss spawns, show warning banner: "⚠ BOSS INCOMING"
- Red pulsing border on screen edges
- If screen shake is enabled: mild shake on boss spawn

---

## DDD Analysis

### Domain Changes
- `EnemyType`: Add `BOSS_DRAGON` with `armor: Int` field
- `Enemy`: Add `armor: Int` field, modify damage calculation
- `StatusEffect`: Add `Enraged(speedBoost: Float, remainingMs: Long)`
- `StatusEffectSystem`: Handle enrage trigger at 30% HP
- `ProjectileSystem`: Apply armor reduction to damage
- `GameEvent`: Add `BossWarning(waveIndex: Int)`, `BossEnraged(enemyId: Int)`
- `WaveSpawnerSystem`: Emit BossWarning 3s before boss spawn

### Modified Aggregates
- `Enemy` entity gains armor field
- `GameEngine` checks for enrage threshold

---

## TDD Specs

### Unit Tests
1. Boss takes reduced damage: 80 damage - 30 armor = 50 actual damage
2. Minimum damage is 1 even with high armor
3. Poison damage bypasses armor entirely
4. Boss enrages at 30% HP: speed increases by 50%
5. Enraged status is applied automatically, not from tower effect
6. Boss warning event emitted 3s before boss wave group spawns
7. Boss reward is 150g (multiplied by goldRewardMultiplier)

### BDD Specs
```
Scenario: Boss takes reduced damage from armor
  Given a boss with 30 armor
  When hit by a projectile dealing 80 damage
  Then the boss takes 50 damage

Scenario: Poison bypasses boss armor
  Given a boss with 30 armor and poisoned status
  When poison ticks for 8 damage
  Then the boss takes 8 damage (not reduced)

Scenario: Boss enrages at low HP
  Given a boss at 31% HP
  When the boss takes damage dropping to 29% HP
  Then the boss gains Enraged status
  And boss speed increases by 50%

Scenario: Boss warning before spawn
  Given the game is playing wave 5 (final wave with boss)
  When 3 seconds remain before boss spawn
  Then a BossWarning event is emitted
```

---

## Clean Architecture

### Engine Layer (engine/)
- `model/Enemy.kt`: add armor field to EnemyType and Enemy
- `model/StatusEffect.kt`: add Enraged variant
- `system/ProjectileSystem.kt`: apply armor reduction
- `system/StatusEffectSystem.kt`: handle enrage trigger + enrage speed boost
- `system/WaveSpawnerSystem.kt`: emit boss warning event
- `event/GameEvent.kt`: add BossWarning, BossEnraged
- `level/Levels.kt`: add BOSS_DRAGON to final waves

### Presentation Layer (feature/game/)
- `renderer/EnemyRenderer.kt`: boss sprite (larger pixel art), armor bar segment
- `vfx/ParticleSystem.kt`: boss death mega-explosion
- `GameScreen.kt`: boss warning banner overlay
- `GameViewModel.kt`: process boss events

---

## Acceptance Criteria
- [ ] Boss appears in final wave of each level
- [ ] Armor visibly reduces damage (except poison)
- [ ] Boss enrages at 30% HP with speed increase
- [ ] Warning banner appears before boss spawn
- [ ] Boss has unique, larger pixel-art sprite
- [ ] Boss death triggers enhanced VFX
- [ ] All existing tests still pass
- [ ] New boss-specific tests all pass
