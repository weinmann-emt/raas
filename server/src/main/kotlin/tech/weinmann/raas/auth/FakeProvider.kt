package tech.weinmann.raas.auth

class FakeProvider(override val backendConfig: Map<*, *>, override val jwtConfig: Map<*, *>) : AuthProviderInterface,
    TokenGenerator() {
    override fun login(user: User): String? {
        return generateToken(user.username, listOf("user"))
    }
}