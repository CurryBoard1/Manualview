package com.example.manualview

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AforoViewModel : ViewModel() {
    private val _state = MutableStateFlow(AforoState())
    val state: StateFlow<AforoState> = _state.asStateFlow()

    fun enter() {
        _state.update { current ->
            if (current.canEnter) {
                current.copy(
                    currentCount = current.currentCount + 1,
                    dailyVisits = current.dailyVisits + 1,
                )
            } else {
                current
            }
        }
    }

    fun exit() {
        _state.update { current ->
            if (current.canExit) {
                current.copy(
                    currentCount = current.currentCount - 1,
                )
            } else {
                current
            }
        }
    }

    fun addGroup(count: Int) {
        _state.update { current ->
            val added = (current.currentCount + count).coerceAtMost(current.maxCapacity) - current.currentCount
            if (added > 0) {
                current.copy(
                    currentCount = current.currentCount + added,
                    dailyVisits = current.dailyVisits + added,
                )
            } else {
                current
            }
        }
    }

    fun reset() {
        _state.value = AforoState()
    }
}
