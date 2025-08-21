package tech.weinmann.raas

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.response.*
import org.koin.ktor.ext.inject
import tech.weinmann.raas.auth.AuthProviderInterface
import tech.weinmann.raas.auth.User
import kotlin.getValue

fun Application.configureAuth(){
    val authService by inject<AuthProviderInterface>()
    install(Authentication) {
        jwt() {
            realm = authService.jwtConfig["realm"] as String
            verifier(
                JWT
                .require(Algorithm.HMAC256(authService.jwtConfig["secret"] as String))
                .withAudience(authService.jwtConfig["audience"] as String)
                .withIssuer(authService.jwtConfig["issuer"] as String)
                .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
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
            authenticate {
                get("/whoami", {
                    description = "Some debug route"
                }){
                    val principal = call.principal<JWTPrincipal>()
                    val username = principal!!.payload.getClaim("username").asString()
                    val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
                    call.respondText("Hello, $username! Token is expired at $expiresAt ms.")
                }
            }
        }

    }

}

