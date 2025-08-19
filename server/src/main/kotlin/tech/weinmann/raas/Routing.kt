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
import io.github.smiley4.ktoropenapi.*


fun Application.configureRouting() {
    val deviceService by inject<DeviceRepoInterface>()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        route("/api/v1"){
            route ("/machine", {
                description = "Operations to be performed by RPI devices"
                
            }){
                get("{serial}", {
                    description = "Get own configuration or 404"
                    response {
                        code(HttpStatusCode.OK) {
                            body<RpiConfiguration> {
                                required = true
                                description = "Your config"
                            }
                        }
                        code(HttpStatusCode.NotFound) {
                            body<String> {
                            }
                        }

                    }
                }){
                    val serial = call.parameters["serial"]!!
                    val conf = deviceService.read(serial = serial)
                    if (conf != null){
                        call.respond(conf)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
                post<RpiConfiguration>("", {
                    description = "Register a new device"
                    request {
                        body<RpiConfiguration> {

                        }
                    }
                    response {
                        code(HttpStatusCode.Created) {
                            body<RpiConfiguration> {  }
                        }
                    }
                }) {
                    val ret = deviceService.create(it)
                    call.respond(HttpStatusCode.Created, ret)

                    }
                }
            }
            // This one need to be protected later
            route("devices"){
                get(""){
                    val list = deviceService.list()
                    call.respond(list)
                }
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
                put<RpiConfiguration> {
                    deviceService.configure(it)
                }
            }
        }
        // This is needed to be possible without registration!

}

