package tech.weinmann.raas.auth

interface AuthProviderInterface {
    val jwtConfig: Map<*, *>
    val backendConfig: Map<* , *>
    fun login(user: User):Map<String, String>?
    fun refreshToken(refreshToken: String): Map<String,String>
}
