package tech.weinmann.raas.client


import kotlinx.serialization.Serializable

@Serializable
data class User(val username: String, val password:String)
