package tech.weinmann.raas.auth
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.jwt.*
import java.util.Date

class LdapProvider(private val audience: String, private val issuer:String, private val secret: String, private val ldap_address: String, private val base_dn: String ) : AuthProviderInterface {
    override fun login(username: String, password: String): String? {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("username", "")
            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
            .sign(Algorithm.HMAC256(secret))
    }
}