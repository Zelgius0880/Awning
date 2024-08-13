package com.zelgius.awning

data class AwningStatus (
    val duration: Long,
    val openingDuration: Long,
    val closingDuration: Long,
    val network: Int,
    val progress: Int?,
    val status: Status
) {
    companion object {
        const val STOP = 0
        const val CLOSE = 1
        const val OPEN = 2
    }
}