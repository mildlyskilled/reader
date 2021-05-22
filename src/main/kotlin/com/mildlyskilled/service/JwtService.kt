package com.mildlyskilled.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.mildlyskilled.model.common.JwtConfig
import org.joda.time.DateTime
import org.joda.time.Duration
import org.slf4j.LoggerFactory

interface JavaJWT {
    fun create(subject: String): String?
    fun verify(token: String): String?
    fun refreshToken(userID: String): String?
}

class JwtService(private val jwtConfig: JwtConfig) : JavaJWT {
    private val log = LoggerFactory.getLogger(JwtService::class.java)
    private val algorithm = Algorithm.HMAC256(jwtConfig.secret)

    override fun create(subject: String): String? {
        return try {
            val expiresAt = DateTime.now().plus(Duration.standardDays(jwtConfig.tokenDuration.toDays()))
            return JWT
                .create()
                .withIssuer(jwtConfig.issuer)
                .withSubject(subject)
                .withExpiresAt(expiresAt.toDate())
                .sign(algorithm)
        } catch (e: JWTCreationException) {
            log.error("Unable to create JWT token", e)
            null
        }
    }

    override fun verify(token: String): String? {
        return try {
            verifier.verify(token).subject
        } catch (e: JWTVerificationException) {
            log.error("Unable to verify JWT token", e)
            null
        }
    }

    override fun refreshToken(userID: String): String? =
        try {
            JWT.create()
                .withSubject(userID)
                .withExpiresAt(
                    DateTime.now().plus(Duration.standardDays(jwtConfig.refreshTokenDuration.toDays())).toDate()
                )
                .sign(algorithm)
        } catch (e: JWTVerificationException) {
            log.info("Unable to verify JWT token", e)
            null
        }

    private val verifier by lazy {
        JWT.require(algorithm).acceptExpiresAt(60).build()
    }
}