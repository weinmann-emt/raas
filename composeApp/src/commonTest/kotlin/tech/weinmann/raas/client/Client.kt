package tech.weinmann.raas.client

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class ComposeAppCommonClientTest {
    @Test
    fun example() = runTest {
        val dut = Client("http://localhost:8080")
        dut.login("test", "test")

    }
}