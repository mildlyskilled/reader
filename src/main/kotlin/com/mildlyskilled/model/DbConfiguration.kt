package com.mildlyskilled.model

data class DbConfiguration(
    val host: String,
    val port: String,
    val user: String,
    val password: String,
    val dbName: String,
    val driver: String,
    val retryAttempts: Int
) {
    val url: String = when (driver) {
        "org.h2.Driver" -> "jdbc:h2:mem:$dbName;MODE=PostgreSQL"
        else -> "jdbc:postgresql://$host:$port/$dbName"
    }
}