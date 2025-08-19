package tech.weinmann.raas

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.koin.core.logger.Level
import org.koin.ktor.plugin.*
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import tech.weinmann.raas.auth.AuthProviderFactory
import tech.weinmann.raas.devices.DeviceRepoInterface
import tech.weinmann.raas.devices.DeviceRepoJdbc

fun Application.configureDependencies() {
    val authValues = environment.config.property("authentication").getMap()
    install(Koin) {
        printLogger(Level.DEBUG)
        modules(
            module {
                single {
                    Database.connect(
                        url = environment.config.property("database.url").getString(),
                        user = environment.config.property("database.user").getString(),
                        password = environment.config.property("database.password").getString(),
                        driver = environment.config.property("database.driver").getString()
                    )
                }

                singleOf(::DeviceRepoJdbc){
                    bind<DeviceRepoInterface>()
                    createdAtStart()
                }
                single {
                    AuthProviderFactory.build(authValues)
                }
            }
        )
    }
}