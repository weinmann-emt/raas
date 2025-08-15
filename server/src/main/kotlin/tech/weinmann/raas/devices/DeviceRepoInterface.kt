package tech.weinmann.raas.devices

import tech.weinmann.raas.configuration.RpiConfiguration


interface DeviceRepoInterface {
    suspend fun read(serial: String): RpiConfiguration?
    suspend fun list(): List<RpiConfiguration>
    suspend fun create(config: RpiConfiguration): RpiConfiguration
    suspend fun configure(config: RpiConfiguration): RpiConfiguration
}
