package tech.weinmann.raas.auth

import io.ktor.server.config.ApplicationConfig
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import kotlin.test.*
import tech.weinmann.raas.database.DeviceDAO
import tech.weinmann.raas.database.DeviceTable
import kotlin.test.BeforeTest

class AuthProviderFactoryTest {
    @Test
    fun factory(){
        val config = ApplicationConfig("application.yaml")
        val authValues = config.property("authentication").getMap()
        val dut = AuthProviderFactory.build(authValues)
        assertTrue { dut is LdapProvider }
    }
}