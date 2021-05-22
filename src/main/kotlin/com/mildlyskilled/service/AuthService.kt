package com.mildlyskilled.service

import com.lambdaworks.crypto.SCryptUtil
import com.mildlyskilled.model.outgoing.Token
import com.mildlyskilled.repository.ReaderRepository
import org.http4k.core.Credentials
import org.slf4j.LoggerFactory

class AuthService(private val readerRepository: ReaderRepository, private val jwtService: JwtService) {
    private val logger = LoggerFactory.getLogger(AuthService::class.java)

    suspend fun authenticate(credentials: Credentials): Token? {
        return readerRepository.getReaderByEmail(credentials.user)?.let {
            if (SCryptUtil.check(credentials.password, it.password)) {
                jwtService.create(it.id.toString())
                    ?.let { tokenString ->
                        Token(
                            token = tokenString,
                            refresh = jwtService.refreshToken(it.id.toString())
                        )
                    }
            } else {
                logger.error("Credentials did not match")
                null
            }
        }
    }

}