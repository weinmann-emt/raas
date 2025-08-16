package tech.weinmann

import io.ktor.http.*

import io.ktor.server.application.*

import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.koin.ktor.ext.inject
import tech.weinmann.raas.devices.DeviceRepoInterface
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.*
import tech.weinmann.raas.configuration.RpiConfiguration


fun Application.configureRouting() {
    val deviceService by inject<DeviceRepoInterface>()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        route("/api/v1"){
            route ("/machine"){
                get("{serial}"){
                    val serial = call.parameters["serial"]!!
                    val conf = deviceService.read(serial = serial)
                    if (conf != null){
                        call.respond(conf)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
                post<RpiConfiguration> {
                    deviceService.create(it)
                }
            }
        }
        // This is needed to be possible without registration!

    }
}
