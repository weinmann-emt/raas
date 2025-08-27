package tech.weinmann.raas

import io.github.smiley4.ktoropenapi.OpenApi

import io.github.smiley4.ktoropenapi.config.AuthScheme
import io.github.smiley4.ktoropenapi.config.AuthType
import io.github.smiley4.ktoropenapi.openApi

import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.ktor.server.application.*
import io.ktor.server.routing.*


fun Application.openApiGen() {
    install(OpenApi){
        security {
            securityScheme("MySecurityScheme") {
                type = AuthType.HTTP
                scheme = AuthScheme.BEARER
                bearerFormat = "jwt"
            }
            // if no other security scheme is specified for a route, the one with this name is used instead
            defaultSecuritySchemeNames("MySecurityScheme")
            // if no other response is documented for "401 Unauthorized", this information is used instead
            defaultUnauthorizedResponse {
                description = "Username or password is invalid"
                body<AuthRequired>()
            }
        }
    }

    routing {
        route("swagger") {
            swaggerUI("/api.json")
        }
        route("api.json") {
            openApi()
        }

    }

}

class AuthRequired(val message: String)

