package com.mildlyskilled.routes

import com.mildlyskilled.model.incoming.ImportRequest
import com.mildlyskilled.model.outgoing.Message
import com.mildlyskilled.model.outgoing.Section
import com.mildlyskilled.model.outgoing.UserFeed
import com.mildlyskilled.parser.Parser
import com.mildlyskilled.routes.filters.AuthFilter
import com.mildlyskilled.service.FeedService
import kotlinx.coroutines.runBlocking
import org.http4k.core.Body
import org.http4k.core.MemoryResponse
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.then
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import java.util.Base64

fun feed(feedService: FeedService, authFilter: AuthFilter): Array<RoutingHttpHandler> {
    val messageLens = Body.auto<Message>().toLens()
    return arrayOf(
        "/feed" bind routes(
            "/{id}" bind Method.GET to authFilter.then { req ->

                req.path("id")?.let { userId ->
                    val responseLens = Body.auto<UserFeed>().toLens()
                    runBlocking { feedService.readerFeed(userId) }?.let { readerFeed ->
                        responseLens(readerFeed, Response(Status.OK))
                    } ?: messageLens(Message("This reader was not found"), Response(Status.NOT_FOUND))
                } ?: messageLens(Message("Provide a valid User ID"), Response(Status.BAD_REQUEST))
            },
            "/section/{id}" bind Method.GET to authFilter.then { request ->
                request.path("id")?.let { sectionId ->
                  val responseLens = Body.auto<Section>().toLens()
                    runBlocking { feedService.getSection(sectionId) }.fold(
                        { error -> messageLens(Message(error.localizedMessage), Response(Status.INTERNAL_SERVER_ERROR))},
                        { sectionNews -> responseLens(sectionNews, Response(Status.OK))})
                }?: messageLens(Message("Provide a valid section id"), Response(Status.BAD_REQUEST))
            },
            "/import" bind Method.POST to authFilter.then { request ->
                val importLens = Body.auto<ImportRequest>().toLens()
                val importRequest = importLens(request)
                try {
                    val import = String(Base64.getDecoder().decode(importRequest.payload))
                    val opml = Parser.parseOpml(MemoryResponse(body = Body(import), status = Status.OK))
                    if (runBlocking { feedService.saveFeed(importRequest.readerId, opml) }) {
                        messageLens(Message("Successfully processed import"), Response(Status.ACCEPTED))
                    } else {
                        messageLens(Message("Could not parse and import feed"), Response(Status.BAD_REQUEST))
                    }
                } catch (e: IllegalArgumentException) {
                    messageLens(Message("Invalid payload"), Response(Status.BAD_REQUEST))
                }
            }
        )
    )
}