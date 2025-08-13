package tech.weinmann.raas.devices

import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import kotlin.test.*
import tech.weinmann.raas.database.DeviceDAO
import tech.weinmann.raas.database.DeviceTable
import kotlin.test.BeforeTest

class DeviceRepoJdbcTest {

    var dut: DeviceRepoJdbc = DeviceRepoJdbc(Database.connect("jdbc:h2:mem:dev_read;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver"))

    init {
        transaction {
            SchemaUtils.create(DeviceTable)
            DeviceDAO.new {
                serial = "1a"
                hostname = "first"
                osUrl = "https://example.com"
                owner = "kari"
            }
        }
    }

    @Test
    fun read() = runTest {
        val ret = dut.read("a1")
        assertEquals("first", ret?.hostname)
    }

    @Test
    fun list() = runTest {
        val ret = dut.list()
        assertTrue(ret.isNotEmpty())
    }

    @Test
    fun create() {
    }

    @Test
    fun configure() {
    }

}