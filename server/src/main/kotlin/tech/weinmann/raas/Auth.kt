package tech.weinmann.raas

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.smiley4.ktoropenapi.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.basic
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.context.GlobalContext.get
import org.koin.core.qualifier.named
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject
import tech.weinmann.raas.auth.AuthProviderInterface
import tech.weinmann.raas.auth.User
import tech.weinmann.raas.configuration.RpiConfiguration
import tech.weinmann.raas.devices.DeviceRepoInterface
import kotlin.getValue

fun Application.configureAuth(){
    val authService by inject<AuthProviderInterface>()
    install(Authentication) {
        jwt("auth-jwt") {
            realm = authService.jwtConfig["realm"] as String
            verifier(
                JWT
                .require(Algorithm.HMAC256(authService.jwtConfig["secret"] as String))
                .withAudience(authService.jwtConfig["realm"] as String)
                .withIssuer(authService.jwtConfig["realm"] as String)
                .build()
            )
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
    routing {
        route("/api/v1/auth"){
            post<User>("", {
                description = "Login and get token"
                request {
                    body<User> {

                    }
                }
                response {
                    code(HttpStatusCode.OK) {
                        body<String> {  }
                    }
                    code(HttpStatusCode.Unauthorized) {
                        description = "User not allowed"
                    }
                }
            }) {
                val ret = authService.login(it)
                if (ret != null){
                    call.respond(HttpStatusCode.OK, ret)
                } else {
                    call.respond(HttpStatusCode.Unauthorized)
                }

            }
        }
        }

    }

