package tech.weinmann.raas.configuration

import kotlinx.serialization.Serializable

@Serializable
data class RpiConfiguration(
    val serial: String,
    val hostname: String,
    val osUrl: String,
    val owner: String
)
