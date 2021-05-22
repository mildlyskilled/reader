package com.mildlyskilled.routes.filters

import com.mildlyskilled.model.outgoing.Message
import com.mildlyskilled.service.JwtService
import com.mildlyskilled.service.ReaderService
import kotlinx.coroutines.runBlocking
import org.http4k.core.Body
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.Jackson.auto

val Request.userId: String?
    get() = this.header("x-user-id")

class AuthFilter(private val jwtService: JwtService, private val readerService: ReaderService) : Filter {
    val messageLens = Body.auto<Message>().toLens()

    override fun invoke(next: HttpHandler): HttpHandler = { req ->
        req.header("Authorization")?.let { token ->
            jwtService.verify(token.split(" ")[1])?.let { userId ->
                runBlocking { readerService.readerById(userId) }?.let { reader ->
                    next(req.header("x-user-id", reader.id.toString()))
                }
            }
        } ?: messageLens(Message("Could not verify authorization header"), Response(Status.UNAUTHORIZED))
    }
}