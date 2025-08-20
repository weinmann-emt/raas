package tech.weinmann.raas.auth

class AuthProviderFactory {
    companion object{
        fun build(config: Map<String, Any?>): AuthProviderInterface?{

            val backend = config["backend"] as Map<*, *>
            val jwt = config["jwt"] as Map<*, *>
            if (backend["provider"] == "ldap"){
                return LdapProvider(
                    backend,
                    jwt
                )
            }
            return null
        }
    }
}