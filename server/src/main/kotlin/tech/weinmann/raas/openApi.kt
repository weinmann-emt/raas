package tech.weinmann.raas

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.application.*


fun Application.openApiGen() {
//    install(SwaggerUI) {
//        // configure basic information about the api
//        info {
//            title = "Example API"
//            description = "An example api to showcase basic swagger-ui functionality."
//        }
//        // configure the servers from where the api is being served
//        server {
//            url = "http://localhost:8080"
//            description = "Development Server"
//        }
//        server {
//            url = "https://www.example.com"
//            description = "Production Server"
//        }
//    }
    install(OpenApi)
    routing {
        route("swagger") {
            swaggerUI("/api.json")
        }
        route("api.json") {
            openApi()
        }

    }
}