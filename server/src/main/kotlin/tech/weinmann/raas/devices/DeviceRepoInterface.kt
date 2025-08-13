package tech.weinmann.raas.devices

import tech.weinmann.raas.configuration.RpiConfiguration


interface DeviceRepoInterface {
    suspend fun read(serial: String): RpiConfiguration?
    suspend fun list(): List<RpiConfiguration>
    suspend fun create(serial: String): Boolean
    suspend fun configure(config: RpiConfiguration): Boolean
}
