package tech.weinmann.raas

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.basic

import org.koin.core.context.GlobalContext.get
import org.koin.core.qualifier.named
import org.koin.ktor.ext.get

fun Application.configureAuth(){
    val conf = get< Map<String, String>>(named("auth_config"))

}
