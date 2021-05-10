package com.mildlyskilled.route

import com.mildlyskilled.model.outgoing.Message
import com.mildlyskilled.model.outgoing.UserFeed
import com.mildlyskilled.service.FeedService
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

fun feed(feedService: FeedService): Array<RoutingHttpHandler> =
    arrayOf(
        "/feed" bind routes(
            "/{id}" bind Method.GET to { req ->
                val messageLens = Body.auto<Message>().toLens()
                req.path("id")?.let { userId ->
                    val responseLens = Body.auto<UserFeed>().toLens()
                    runBlocking { feedService.readerFeed(userId) }?.let { readerFeed ->
                        responseLens(readerFeed, Response(Status.OK))
                    } ?: messageLens(Message("This reader was not found"), Response(Status.NOT_FOUND))

                } ?: messageLens(Message("Provide a valid User ID"), Response(Status.BAD_REQUEST))

            },
            "/import" bind Method.POST to { request ->
                println(request.body)
                Response(Status.ACCEPTED).body("Feed uploaded")
            }
        )
    )