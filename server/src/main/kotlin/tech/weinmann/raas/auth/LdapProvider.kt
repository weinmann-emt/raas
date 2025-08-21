package tech.weinmann.raas.auth
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.*
import java.util.*
import javax.naming.Context
import javax.naming.NamingEnumeration
import javax.naming.directory.InitialDirContext
import javax.naming.directory.SearchControls


class LdapProvider(private val ldapConfig: Map<*, *>, private val jwtConfig: Map<*, *>) : AuthProviderInterface {
    lateinit var ctx: InitialDirContext

    private fun connect(){
        System.setProperty("javax.net.ssl.keyStore", "NONE");
        System.setProperty("javax.net.ssl.keyStoreType", "Windows-my");
        System.setProperty("javax.net.ssl.trustStore", "NONE");
        System.setProperty("javax.net.ssl.trustStoreType", "Windows-ROOT");
        // Check is the passwd is an env var
        var bind_pass = ldapConfig["bind_pass"] as String?
        if (bind_pass?.lowercase() == "env"){
            bind_pass = System.getenv("LDAP_BIND_PASS")
        }
        val env = Hashtable<String, String>()
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple")
        env.put(Context.PROVIDER_URL, ldapConfig["address"] as String?);
        env.put(Context.SECURITY_PRINCIPAL, ldapConfig["bind_user"] as String?);
        env.put(Context.SECURITY_CREDENTIALS, bind_pass);

        try {
            ctx = InitialDirContext(env);
        } catch (ex: javax.naming.NamingException) {
            throw ex
        }
    }

    private fun getUserObject(username: String, password: String){
        val searchControls = SearchControls()
        searchControls.returningAttributes = arrayOf("cn");
        searchControls.searchScope = SearchControls.SUBTREE_SCOPE

        val ergebnis: NamingEnumeration<*>? =
            ctx.search(
                ldapConfig["base_dn"] as String?,
                "(&(objectClass=person)(sAMAccountName=$username))",
                searchControls
            )
    }

    private fun getRoles(): List<String>{
        return emptyList()
    }

    override fun login(username: String, password: String): String? {
        if(!this::ctx.isInitialized){
            connect()
        }


        return JWT.create()
            .withAudience(jwtConfig["audience"]as String?)
            .withIssuer(jwtConfig["issuer"]as String?)
            .withClaim("username", "")
            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
            .sign(Algorithm.HMAC256(jwtConfig["secret"]as String?))
    }
}