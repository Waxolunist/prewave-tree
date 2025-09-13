package com.prewave.sterzl.supplychain.model

data class BranchDTO(
    val from: Int,
    val to: Array<Int>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BranchDTO

        if (from != other.from) return false
        if (!to.contentEquals(other.to)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = from
        result = 31 * result + to.contentHashCode()
        return result
    }
}
