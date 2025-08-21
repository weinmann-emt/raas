package tech.weinmann.raas.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*


abstract class TokenGenerator: AuthProviderInterface {
    protected fun generateToken(username: String, roles:List<String>): String{
        return JWT.create()
            .withAudience(jwtConfig["audience"]as String?)
            .withIssuer(jwtConfig["issuer"]as String?)
            .withClaim("username", username)
            .withClaim("roles", roles)
            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
            .sign(Algorithm.HMAC256(jwtConfig["secret"]as String?))
    }
}