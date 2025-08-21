package tech.weinmann.raas.auth

class FakeProvider(override val backendConfig: Map<*, *>, override val jwtConfig: Map<*, *>) : AuthProviderInterface,
    TokenGenerator() {
    override fun login(user: User): Map<String, String> {
        val accessToken =generateToken(user.username, listOf("user"), 60)
        val refreshToken =generateToken(user.username, listOf("user"), 60)
        return mapOf("accessToken" to accessToken,
        "refreshToken" to refreshToken)
    }
}