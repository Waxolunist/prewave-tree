package com.prewave.sterzl.supplychain.model

import jakarta.validation.constraints.Positive

data class EdgeDTO(
    @field:Positive(message = "From value must be positive")
    val from: Int,
    @field:Positive(message = "To value must be positive")
    val to: Int,
) {
    // For Jackson only
    private constructor() : this(0, 0)
}
