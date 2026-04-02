package com.pixelfort.towerdefense.feature.game.tutorial

/**
 * Defines where the spotlight should focus for each tutorial step.
 */
enum class SpotlightTarget {
    FULL_SCREEN,
    MAP_AREA,
    TOWER_BAR,
    BUILDABLE_CELLS,
    START_BUTTON,
    PLACED_TOWER
}

/**
 * Represents a single step in the first-play tutorial.
 */
data class TutorialStep(
    val index: Int,
    val text: String,
    val spotlightTarget: SpotlightTarget,
    val textAtBottom: Boolean = true
)

/**
 * All 7 tutorial steps as defined in SPEC-019.
 */
val TUTORIAL_STEPS: List<TutorialStep> = listOf(
    TutorialStep(0, "Welcome to PixelFort!", SpotlightTarget.FULL_SCREEN, textAtBottom = false),
    TutorialStep(1, "Enemies follow the path from top to bottom", SpotlightTarget.MAP_AREA),
    TutorialStep(2, "Tap a tower to select it for placement", SpotlightTarget.TOWER_BAR),
    TutorialStep(3, "Tap a green cell to place your tower", SpotlightTarget.BUILDABLE_CELLS),
    TutorialStep(4, "Press to send the first wave of enemies", SpotlightTarget.START_BUTTON),
    TutorialStep(5, "Tap a placed tower to upgrade or sell it", SpotlightTarget.PLACED_TOWER),
    TutorialStep(6, "Good luck, Commander!", SpotlightTarget.FULL_SCREEN, textAtBottom = false)
)

/**
 * Holds the runtime state of the tutorial.
 */
data class TutorialState(
    val currentStepIndex: Int = 0,
    val isActive: Boolean = true,
    val isCompleted: Boolean = false
) {
    val currentStep: TutorialStep?
        get() = if (isActive && currentStepIndex in TUTORIAL_STEPS.indices) {
            TUTORIAL_STEPS[currentStepIndex]
        } else null

    val totalSteps: Int get() = TUTORIAL_STEPS.size

    fun advance(): TutorialState {
        if (!isActive || isCompleted) return this
        val nextIndex = currentStepIndex + 1
        return if (nextIndex >= TUTORIAL_STEPS.size) {
            copy(isActive = false, isCompleted = true)
        } else {
            copy(currentStepIndex = nextIndex)
        }
    }

    fun skip(): TutorialState {
        return copy(isActive = false, isCompleted = true)
    }

    companion object {
        /** Tutorial starts inactive if already completed. */
        fun initial(alreadyCompleted: Boolean): TutorialState {
            return if (alreadyCompleted) {
                TutorialState(isActive = false, isCompleted = true)
            } else {
                TutorialState(currentStepIndex = 0, isActive = true, isCompleted = false)
            }
        }
    }
}
