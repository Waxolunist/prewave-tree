package com.prewave.sterzl.supplychain.model

data class EdgeDTO(
    val from: Int,
    val to: Int,
) {
    // For Jackson only
    private constructor() : this(0, 0)
}
