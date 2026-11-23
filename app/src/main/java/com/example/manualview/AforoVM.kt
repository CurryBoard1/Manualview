package com.example.manualview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AforoViewModel : ViewModel() {
    var state by mutableStateOf(AforoState())
        private set

    fun enter() {
        if (state.canEnter) {
            state = state.copy(
                currentCount = state.currentCount + 1,
                dailyVisits = state.dailyVisits + 1,
            )
        }
    }

    fun exit() {
        if (state.canExit) {
            state = state.copy(
                currentCount = state.currentCount - 1,
            )
        }
    }

    fun addGroup(count: Int) {
        val added = (state.currentCount + count).coerceAtMost(state.maxCapacity) - state.currentCount
        if (added > 0) {
            state = state.copy(
                currentCount = state.currentCount + added,
                dailyVisits = state.dailyVisits + added,
            )
        }
    }

    fun reset() {
        state = AforoState()
    }
}
