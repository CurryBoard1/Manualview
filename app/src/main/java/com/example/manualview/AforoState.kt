package com.example.manualview

const val MAX_CAPACITY = 50

data class AforoState(
    val currentCount: Int = 0,
    val dailyVisits: Int = 0,
    val maxCapacity: Int = MAX_CAPACITY
) {
    val percentage: Float get() = currentCount / maxCapacity.toFloat()
    val canEnter: Boolean get() = currentCount < maxCapacity
    val canExit: Boolean get() = currentCount > 0
}
