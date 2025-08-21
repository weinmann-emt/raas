package tech.weinmann.raas.auth
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.*
import java.util.*
import javax.naming.Context
import javax.naming.NamingEnumeration
import javax.naming.NamingException
import javax.naming.directory.InitialDirContext
import javax.naming.directory.SearchControls
import javax.naming.directory.SearchResult
import javax.naming.ldap.LdapContext




class LdapProvider(private val ldapConfig: Map<*, *>, override val jwtConfig: Map<*, *>) : AuthProviderInterface {
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
        } catch (ex: NamingException) {
            throw ex
        }
    }

    private fun getUserObject(username: String): String{
        val searchControls = SearchControls()
        searchControls.returningAttributes = arrayOf("cn");
        searchControls.searchScope = SearchControls.SUBTREE_SCOPE

        val result: NamingEnumeration<*>? =
            ctx.search(
                ldapConfig["base_dn"] as String?,
                "(&(objectClass=person)(sAMAccountName=$username))",
                searchControls
            )
        if (result == null){
            throw NamingException("User not found")
        }
        val test = result.next() as SearchResult
        return test.nameInNamespace
    }

    //This can be adopted once we have more than simple manage of pi
    private fun getRoles(): List<String>{
        val ret = listOf("user")
        return ret
    }

    public fun authenticate(dn: String, pass:String){
        val env = Hashtable<String, String>()
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple")
        env.put(Context.PROVIDER_URL, ldapConfig["address"] as String?);
        env.put(Context.SECURITY_PRINCIPAL, dn);
        env.put(Context.SECURITY_CREDENTIALS, pass);

        try {
            ctx = InitialDirContext(env);
        } catch (ex: NamingException) {
            throw ex
        }
    }

    override fun login(user: User): String? {
        if(!this::ctx.isInitialized){
            connect()
        }
        try {
            val cn =getUserObject(user.username)
            authenticate(cn, user.password)
        } catch (ex: NamingException) {
            return null
        }
        val roles = getRoles()

        return JWT.create()
            .withAudience(jwtConfig["audience"]as String?)
            .withIssuer(jwtConfig["issuer"]as String?)
            .withClaim("username", user.username)
            .withClaim("roles", roles)
            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
            .sign(Algorithm.HMAC256(jwtConfig["secret"]as String?))
    }
}

