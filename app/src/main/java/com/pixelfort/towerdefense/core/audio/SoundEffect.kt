package com.pixelfort.towerdefense.core.audio

import com.pixelfort.towerdefense.R

enum class SoundEffect(val resId: Int, val priority: Int) {
    TOWER_PLACED(R.raw.sfx_tower_placed, 5),
    TOWER_FIRE_ARCHER(R.raw.sfx_tower_fire_archer, 3),
    TOWER_FIRE_CANNON(R.raw.sfx_tower_fire_cannon, 3),
    TOWER_FIRE_MAGIC(R.raw.sfx_tower_fire_magic, 3),
    TOWER_FIRE_SNIPER(R.raw.sfx_tower_fire_sniper, 3),
    TOWER_FIRE_FROST(R.raw.sfx_tower_fire_frost, 3),
    TOWER_FIRE_LIGHTNING(R.raw.sfx_tower_fire_lightning, 3),
    TOWER_FIRE_POISON(R.raw.sfx_tower_fire_poison, 3),
    TOWER_FIRE_BOMB(R.raw.sfx_tower_fire_bomb, 3),
    PROJECTILE_HIT(R.raw.sfx_projectile_hit, 3),
    ENEMY_KILLED(R.raw.sfx_enemy_killed, 5),
    ENEMY_REACHED_END(R.raw.sfx_enemy_reached_end, 8),
    WAVE_START(R.raw.sfx_wave_start, 8),
    WAVE_COMPLETE(R.raw.sfx_wave_complete, 8),
    VICTORY(R.raw.sfx_victory, 10),
    DEFEAT(R.raw.sfx_defeat, 10),
    BUTTON_TAP(R.raw.sfx_button_tap, 3),
    TOWER_UPGRADE(R.raw.sfx_tower_upgrade, 5),
    BOSS_WARNING(R.raw.sfx_boss_warning, 8)
}
