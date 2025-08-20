package tech.weinmann.raas.auth

interface AuthProviderInterface {
    fun login(username: String, password: String):String?
}