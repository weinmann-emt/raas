package tech.weinmann.raas.auth

interface AuthProviderInterface {
    val jwtConfig: Map<*, *>
    fun login(user: User):String?
}
