# SPEC-027: Attack Animations & Visual Effects

**Status**: READY
**Branch**: `feature/027-attack-vfx`
**Priority**: Medium
**Depends on**: SPEC-024 (Sprite Art Integration), SPEC-010 (VFX)

---

## Requirements

### R1: Tower Attack Animation
- When tower fires, apply brief scale pulse: 1.0x → 1.15x → 1.0x over 150ms
- Track `lastFireTimeMs` per tower in snapshot
- In `TowerRenderer`: calculate scale based on time since last fire
- Use `drawImage()` with scaled `dstSize`

### R2: Tower Idle Animation
- Subtle breathing animation: scale oscillates 0.98x ↔ 1.02x over 2s cycle
- Use `sin(elapsedMs * PI / 1000)` for smooth oscillation
- Apply to all towers continuously

### R3: Projectile Trail Effects
- Archer arrows: small white trail particles
- Cannon balls: orange fire trail
- Magic projectiles: purple sparkle trail
- Frost projectiles: ice crystal trail (light blue)
- Lightning: bright yellow flash trail
- Poison: green bubble trail
- Bomb: dark smoke trail

### R4: Enemy Hit Flash
- When enemy takes damage, flash white for 80ms
- Use `ColorFilter.tint(Color.White, BlendMode.SrcAtop)` during flash
- Track `lastHitTimeMs` per enemy

### R5: Tower Type-Specific Attack VFX
- Archer: arrow projectile with rotation toward target
- Cannon: explosion particle burst at impact point (existing)
- Magic: purple energy ring expanding at impact
- Frost: ice crystal burst at impact + slow visual on enemy
- Lightning: chain lightning visual between targets
- Poison: green cloud lingering at impact
- Bomb: large explosion with screen shake (existing)

## Implementation Approach
- All effects use existing `ParticleSystem` + `DrawScope` — **no additional image assets needed**
- Attack animations use code-driven scale/rotation transforms on existing sprites
- Projectile trails use `ParticleEmitter` with type-specific colors

## Files to Modify
| File | Change |
|------|--------|
| `feature/game/renderer/TowerRenderer.kt` | Add scale pulse + idle breathing |
| `feature/game/renderer/EnemyRenderer.kt` | Add hit flash effect |
| `feature/game/renderer/ProjectileRenderer.kt` | Add trail particles |
| `feature/game/vfx/ParticleSystem.kt` | Add new preset effects per tower type |
| `engine/model/Tower.kt` or `GameSnapshot.kt` | Expose `lastFireTimeMs` if needed |

## Acceptance Criteria
- [ ] Towers visually pulse when attacking
- [ ] Towers have subtle idle breathing animation
- [ ] Projectiles leave colored trails matching tower type
- [ ] Enemies flash white when hit
- [ ] Attack impacts have type-specific particle effects
- [ ] All effects run at 60fps without frame drops
