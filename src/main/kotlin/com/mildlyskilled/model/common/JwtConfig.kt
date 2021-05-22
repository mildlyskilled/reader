package com.mildlyskilled.model.common

import java.time.Duration


data class JwtConfig(
    val issuer: String,
    val secret: String,
    val tokenDuration: Duration,
    val refreshTokenDuration: Duration
)