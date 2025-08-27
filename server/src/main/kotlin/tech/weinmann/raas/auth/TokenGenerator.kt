package tech.weinmann.raas.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*


abstract class TokenGenerator: AuthProviderInterface {
    protected fun generateToken(username: String, roles:List<String>, expirySeconds: Int): String{
        return JWT.create()
            .withAudience(jwtConfig["audience"]as String?)
            .withIssuer(jwtConfig["issuer"]as String?)
            .withClaim("username", username)
            .withClaim("roles", roles)
            .withExpiresAt(Date(System.currentTimeMillis() + (expirySeconds*1000)))
            .sign(Algorithm.HMAC256(jwtConfig["secret"]as String?))
    }

    protected fun generatePair(username: String, roles:List<String>, expirySeconds: Int): Map<String, String>{
        val accessToken =generateToken(username, roles, expirySeconds)
        val refreshToken =generateToken(username, roles, expirySeconds*10)
        return mapOf(
            "accessToken" to accessToken,
            "refreshToken" to refreshToken
        )
    }

    override fun refreshToken(refreshToken: String): Map<String,String>{
        val token = JWT
            .require(Algorithm.HMAC256(jwtConfig["secret"]as String?))
            .withAudience(jwtConfig["audience"]as String?)
            .withIssuer(jwtConfig["issuer"]as String?)
            .build()
            .verify(refreshToken)
        val username = token.getClaim("username").asString()
        val roles = token.getClaim("roles").asList(String::class.java)
        return generatePair(username, roles, 60)

    }
}