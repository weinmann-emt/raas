package tech.weinmann.raas

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform