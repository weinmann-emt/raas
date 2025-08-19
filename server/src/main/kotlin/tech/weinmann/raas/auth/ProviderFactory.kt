package tech.weinmann.raas.auth

class AuthProviderFactory {
    companion object{
        fun build(config: Map<String, Any?>): AuthProviderInterface?{

            val backend = config["backend"] as Map<*, *>
            val jwt = config["jwt"] as Map<*, *>
            if (backend["provider"] == "ldap"){
                return LdapProvider(
                    jwt["audience"].toString(),
                    issuer = jwt["issuer"].toString(),
                    secret = jwt["secret"].toString(),
                    ldap_address = backend["ldap_address"].toString(),
                    base_dn = backend["base_dn"].toString(),
                )
            }
            return null
        }
    }
}