package com.pixelfort.towerdefense.engine.event

class GameEventBus {
    private val _events = mutableListOf<GameEvent>()
    val events: List<GameEvent> get() = _events.toList()

    fun emit(event: GameEvent) {
        _events.add(event)
    }

    fun clear() {
        _events.clear()
    }
}
