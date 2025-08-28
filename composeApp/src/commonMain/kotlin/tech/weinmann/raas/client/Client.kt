package tech.weinmann.raas.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.post
import io.ktor.client.request.setBody

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.json

class Client(private val base: String) {
    private var client = HttpClient(){
        install(ContentNegotiation) {
            json()
        }
        defaultRequest {
            url("$base/api/v1/")
        }
    }
    suspend fun login(username: String, password: String) {
        val login = User(username, password)
        val response = client.post("auth"){
            contentType(ContentType.Application.Json)
            setBody(login)
        }
        if (response.status != HttpStatusCode.OK){
            throw Exception()
        }
        val tokens = response.body< Map<String, String>?>()
        client = HttpClient(){
            install(ContentNegotiation) {
                json()
            }
            defaultRequest {
                url("$base/api/v1/")
            }
            install(Auth){
                bearer{
                    loadTokens {
                        BearerTokens(tokens?.get("accessToken") ?: "", tokens?.get("refreshToken"))
                    }
                }
            }
        }
    }
}
