package com.mildlyskilled.routes

import com.mildlyskilled.model.incoming.NewReaderRequest
import com.mildlyskilled.model.outgoing.Message
import com.mildlyskilled.model.outgoing.Reader
import com.mildlyskilled.model.outgoing.Token
import com.mildlyskilled.routes.filters.AuthFilter
import com.mildlyskilled.routes.filters.userId
import com.mildlyskilled.service.AuthService
import com.mildlyskilled.service.ReaderService
import kotlinx.coroutines.runBlocking
import org.http4k.core.Body
import org.http4k.core.Credentials
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.then
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes

fun reader(readerService: ReaderService, authFilter: AuthFilter, authService: AuthService): Array<RoutingHttpHandler> {
    val messageLens = Body.auto<Message>().toLens()
    val credentialsLens = Body.auto<Credentials>().toLens()
    val tokenLens = Body.auto<Token>().toLens()
    val readerLens = Body.auto<Reader>().toLens()
    val requestLens = Body.auto<NewReaderRequest>().toLens()

    return arrayOf(
        "/reader" bind routes(
            "/authenticate" bind Method.POST to { request ->
                val authRequest = credentialsLens(request)
                runBlocking { authService.authenticate(authRequest) }?.let {
                    tokenLens(it, Response(Status.OK))
                } ?: messageLens(Message("Did not find a user with these credentials"), Response(Status.NOT_FOUND))

            },

            "/{id}" bind Method.GET to authFilter.then { request ->
                request.path("id")?.let { requestedId ->
                    if (requestedId != request.userId) {
                        return@then messageLens(
                            Message("You are not allowed to access this reader data"),
                            Response(Status.UNAUTHORIZED)
                        )
                    }
                    val reader = runBlocking { readerService.readerById(requestedId) }
                    reader?.let {
                        readerLens(it.toOutgoing(), Response(Status.OK))
                    }
                } ?: messageLens(Message("An ID is required"), Response(Status.BAD_REQUEST))
            },

            "/new" bind Method.POST to { req ->
                val newReaderRequest = requestLens(req)
                val newReader = runBlocking { readerService.newReader(newReaderRequest) }
                readerLens(newReader.toOutgoing(), Response(Status.ACCEPTED))
            }
        )
    )
}
