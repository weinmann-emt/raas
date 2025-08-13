package tech.weinmann.raas.database

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import tech.weinmann.raas.configuration.RpiConfiguration

object DeviceTable: IntIdTable("configurations"){
    val serial = varchar("serial", 20)
    val hostname = varchar("hostname", 20)
    val osUrl = varchar("osUrl", 50)
    val owner = varchar("owner", 5)
}

class DeviceDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DeviceDAO>(DeviceTable)

    var serial by DeviceTable.serial
    var hostname by DeviceTable.hostname
    var osUrl by DeviceTable.osUrl
    var owner by DeviceTable.owner
}

fun DaoToDevice(dao: DeviceDAO): RpiConfiguration = RpiConfiguration(
    dao.serial,
    dao.hostname,
    dao.osUrl,
    dao.owner,
)
