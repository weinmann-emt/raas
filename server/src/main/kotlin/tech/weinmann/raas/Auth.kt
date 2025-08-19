package tech.weinmann.raas

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.basic
import io.ktor.server.auth.ldap.ldapAuthenticate
import org.koin.core.context.GlobalContext.get
import org.koin.core.qualifier.named
import org.koin.ktor.ext.get

fun Application.configureAuth(){
    val conf = get< Map<String, String>>(named("auth_config"))
    if (conf["provider"] == "ldap"){
        install(Authentication){
            basic {
                validate { credentials ->
                    ldapAuthenticate(credentials,conf["address"]!!, conf["user_dn"]!! )
                }
            }
        }
    }
}
