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




class LdapProvider(override val backendConfig: Map<*, *>, override val jwtConfig: Map<*, *>) : AuthProviderInterface, TokenGenerator(){
    lateinit var ctx: InitialDirContext
    

    private fun connect(){
        System.setProperty("javax.net.ssl.keyStore", "NONE");
        System.setProperty("javax.net.ssl.keyStoreType", "Windows-my");
        System.setProperty("javax.net.ssl.trustStore", "NONE");
        System.setProperty("javax.net.ssl.trustStoreType", "Windows-ROOT");
        // Check is the passwd is an env var
        var bind_pass = backendConfig["bind_pass"] as String?
        if (bind_pass?.lowercase() == "env"){
            bind_pass = System.getenv("LDAP_BIND_PASS")
        }
        var bind_user = backendConfig["bind_user"] as String?
        if (bind_pass?.lowercase() == "env"){
            bind_pass = System.getenv("LDAP_BIND_USER")
        }
        val env = Hashtable<String, String>()
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple")
        env.put(Context.PROVIDER_URL, backendConfig["address"] as String?);
        env.put(Context.SECURITY_PRINCIPAL, bind_user);
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
                backendConfig["base_dn"] as String?,
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
        env.put(Context.PROVIDER_URL, backendConfig["address"] as String?);
        env.put(Context.SECURITY_PRINCIPAL, dn);
        env.put(Context.SECURITY_CREDENTIALS, pass);

        try {
            ctx = InitialDirContext(env);
        } catch (ex: NamingException) {
            throw ex
        }
    }

    override fun login(user: User): Map<String, String>? {
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
        return generatePair(user.username, roles, 60)

    }
}

