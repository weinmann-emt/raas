package tech.weinmann.raas.devices

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import tech.weinmann.raas.configuration.RpiConfiguration
import tech.weinmann.raas.database.DaoToDevice
import tech.weinmann.raas.database.DeviceDAO
import tech.weinmann.raas.database.DeviceTable
import tech.weinmann.raas.database.suspendTransaction

class DeviceRepoJdbc(database: Database): DeviceRepoInterface {

    init {
        transaction(database) {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(DeviceTable)
        }
    }

    override suspend fun read(serial: String): RpiConfiguration? = suspendTransaction {
        DeviceDAO
            .find { DeviceTable.serial eq serial }
            .limit(1)
            .map(::DaoToDevice)
            .firstOrNull()
    }

    override suspend fun list(): List<RpiConfiguration> = suspendTransaction {
        DeviceDAO
            .all()
            .map(::DaoToDevice)
    }

    override suspend fun create(config: RpiConfiguration): RpiConfiguration  = suspendTransaction{
        DaoToDevice(
            DeviceDAO.new {
                serial = config.serial
            }
        )
    }

    override suspend fun configure(config: RpiConfiguration): RpiConfiguration = suspendTransaction {
        DaoToDevice(
            DeviceDAO.findSingleByAndUpdate(DeviceTable.serial eq config.serial){
                it.owner = config.owner
                it.hostname = config.hostname
                it.osUrl = config.osUrl
            }!!
        )
    }
}


