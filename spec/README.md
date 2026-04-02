# PixelFort Spec System

## Workflow

```
1. Read spec -> confirm requirements
2. Create feature branch from spec ID
3. DDD -> (TDD -> BDD) -> Clean Architecture loop
4. Tests pass -> ask user if requirements are met
5. Commit + push to GitHub
6. Mark spec COMPLETED
7. Wait for next spec / new version
8. Go to 1
```

## Spec Status Legend

| Status | Meaning |
|--------|---------|
| `COMPLETED` | Implemented, tested, merged |
| `READY` | Approved, ready to develop |
| `DRAFT` | Needs review before development |

## Spec Index

### Completed (Phase 1-7)

| ID | Title | Status | Branch |
|----|-------|--------|--------|
| SPEC-001 | Project Skeleton | COMPLETED | main |
| SPEC-002 | Domain Models (TDD) | COMPLETED | main |
| SPEC-003 | Game Systems (TDD+BDD) | COMPLETED | main |
| SPEC-004 | GameEngine Integration | COMPLETED | main |
| SPEC-005 | UI Rendering MVP | COMPLETED | main |
| SPEC-006 | HUD and Game Flow | COMPLETED | main |
| SPEC-007 | Menus, Navigation, Persistence | COMPLETED | main |
| SPEC-008 | 8 Tower Types + Effects System | COMPLETED | main |
| SPEC-009 | Meta Progression System | COMPLETED | main |
| SPEC-010 | VFX Particle System + Pixel Art | COMPLETED | main |
| SPEC-011 | Mobile Adaptive UI + Tooltips + Level Select | COMPLETED | main |

### Bugfix (Priority)

| ID | Title | Status | Branch |
|----|-------|--------|--------|
| BUG-001 | Enemy Path Alignment + Direction Indicator | COMPLETED | fix/bug-001-path-alignment |
| BUG-002 | Tower Tooltip as Floating Overlay | COMPLETED | fix/bug-002-tooltip-overlay |
| BUG-003 | Status Bar Overlapping UI Buttons | READY | - |

### Ready to Develop

| ID | Title | Status | Branch |
|----|-------|--------|--------|
| SPEC-012 | Game Speed Control + Wave Preview | COMPLETED | feature/012-speed-wave-preview |
| SPEC-013 | Floating Text + Damage Numbers | READY | - |
| SPEC-014 | Screen Shake + Impact Juice | COMPLETED | feature/014-screen-shake |
| SPEC-015 | Boss Mechanics | READY | - |
| SPEC-016 | Difficulty Modes + Endless Mode | READY | - |
| SPEC-017 | Audio System | READY | - |
| SPEC-018 | Settings Screen | READY | - |
| SPEC-019 | Tutorial System | READY | - |
| SPEC-020 | Tower Placement Preview + Turret Rotation | READY | - |
| SPEC-021 | Advanced VFX (Trails, Animations, Environment) | READY | - |
| SPEC-022 | Content Balance + More Levels | READY | - |
| SPEC-023 | Polish and Release | READY | - |
| SPEC-024 | Sprite Art Integration (PNG Assets) | IN PROGRESS | feature/024-sprite-art-integration |
| SPEC-025 | Sprite Quality Fix (去背 + Consistency) | READY | - |
| SPEC-026 | Extra Assets Integration (Icon, BG, Result) | READY | - |
| SPEC-027 | Attack Animations & VFX | READY | - |
