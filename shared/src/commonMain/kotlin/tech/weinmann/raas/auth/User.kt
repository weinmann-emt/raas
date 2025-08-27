package tech.weinmann.raas.auth


import kotlinx.serialization.Serializable

@Serializable
data class User(val username: String, val groups: List<String>)
