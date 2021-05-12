package com.mildlyskilled.routes

import com.mildlyskilled.model.incoming.NewReaderRequest
import com.mildlyskilled.model.outgoing.Message
import com.mildlyskilled.model.outgoing.Reader
import com.mildlyskilled.service.ReaderService
import kotlinx.coroutines.runBlocking
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes

fun reader(readerService: ReaderService): Array<RoutingHttpHandler> =
    arrayOf(
        "/reader" bind routes(
            "/{id}" bind Method.GET to { request ->
                val readerLens = Body.auto<Reader>().toLens()
                val messageLens = Body.auto<Message>().toLens()
                request.path("id")?.let { id ->
                    val reader = runBlocking { readerService.getReader(id) }
                    reader?.let {
                        readerLens(it.toOutgoing(), Response(Status.OK))
                    } ?: messageLens(Message("user not found"), Response(Status.NOT_FOUND))
                } ?: messageLens(Message("An ID is required"), Response(Status.BAD_REQUEST))
            },
            "/new" bind Method.POST to { req ->
                val requestLens = Body.auto<NewReaderRequest>().toLens()
                val readerLens = Body.auto<Reader>().toLens()
                val newReaderRequest = requestLens(req)
                val newReader = runBlocking { readerService.newReader(newReaderRequest) }
                readerLens(newReader.toOutgoing(), Response(Status.ACCEPTED))
            }
        )
    )