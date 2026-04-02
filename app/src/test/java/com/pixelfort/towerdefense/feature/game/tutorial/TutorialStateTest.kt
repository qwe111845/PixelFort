package com.pixelfort.towerdefense.feature.game.tutorial

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TutorialStateTest {

    @Test
    fun `initial state starts at step 0 when not completed`() {
        val state = TutorialState.initial(alreadyCompleted = false)
        assertEquals(0, state.currentStepIndex)
        assertTrue(state.isActive)
        assertFalse(state.isCompleted)
        assertNotNull(state.currentStep)
        assertEquals("Welcome to PixelFort!", state.currentStep?.text)
    }

    @Test
    fun `advance increments step`() {
        val state = TutorialState.initial(alreadyCompleted = false)
        val next = state.advance()
        assertEquals(1, next.currentStepIndex)
        assertTrue(next.isActive)
        assertFalse(next.isCompleted)
    }

    @Test
    fun `skip marks completed`() {
        val state = TutorialState.initial(alreadyCompleted = false)
        val skipped = state.skip()
        assertFalse(skipped.isActive)
        assertTrue(skipped.isCompleted)
    }

    @Test
    fun `tutorial does not trigger if already completed`() {
        val state = TutorialState.initial(alreadyCompleted = true)
        assertFalse(state.isActive)
        assertTrue(state.isCompleted)
        assertNull(state.currentStep)
    }

    @Test
    fun `advancing past last step completes tutorial`() {
        var state = TutorialState.initial(alreadyCompleted = false)
        repeat(TUTORIAL_STEPS.size) {
            state = state.advance()
        }
        assertFalse(state.isActive)
        assertTrue(state.isCompleted)
        assertNull(state.currentStep)
    }

    @Test
    fun `advance on completed state is no-op`() {
        val completed = TutorialState(isActive = false, isCompleted = true)
        val result = completed.advance()
        assertEquals(completed, result)
    }

    @Test
    fun `totalSteps returns 7`() {
        val state = TutorialState.initial(alreadyCompleted = false)
        assertEquals(7, state.totalSteps)
    }
}
