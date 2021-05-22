package com.mildlyskilled

import com.mildlyskilled.listener.ServiceTestListener
import com.mildlyskilled.model.incoming.ImportRequest
import com.mildlyskilled.model.incoming.NewReaderRequest
import com.mildlyskilled.model.outgoing.Message
import com.mildlyskilled.model.outgoing.Section
import com.mildlyskilled.model.outgoing.Token
import com.mildlyskilled.model.outgoing.UserFeed
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.http4k.core.Body
import org.http4k.core.Credentials
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.ACCEPTED
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.format.Jackson.auto
import java.util.Base64
import java.util.UUID
import com.mildlyskilled.model.outgoing.Reader as MildlySkilledReader

class ReaderTest : ShouldSpec({
    listener(ServiceTestListener)

    val newReaderRequest = NewReaderRequest(
        firstName = "Joe",
        lastName = "Bloggs",
        email = "joe.bloggs@mildlyskilled.com",
        password = "secret"
    )

    var reader: MildlySkilledReader? = null
    var token: String? = null
    var newsSectionId: UUID? = null

    context("Reader application") {
        val newReaderRequestLens = Body.auto<NewReaderRequest>().toLens()
        val readerLens = Body.auto<MildlySkilledReader>().toLens()
        val tokenLens = Body.auto<Token>().toLens()
        val userFeedLens = Body.auto<UserFeed>().toLens()
        val sectionLens = Body.auto<Section>().toLens()

        should("register a new reader") {
            val newReaderResponse = app(
                Request(POST, "/reader/new").body(
                    newReaderRequestLens(
                        newReaderRequest,
                        Response(ACCEPTED)
                    ).bodyString()
                )
            )
            reader = readerLens(newReaderResponse)
            reader?.firstName shouldBe "Joe"
            reader?.lastName shouldBe "Bloggs"
            reader?.email shouldBe "joe.bloggs@mildlyskilled.com"
        }

        should("return UNAUTHORIZED from the user endpoint without a valid token") {
            app(Request(GET, "/reader/${reader?.id}")).status shouldBe UNAUTHORIZED
        }

        should("authenticate given a user and a password") {
            val authRequest = Credentials(reader!!.email, newReaderRequest.password)
            val requestLens = Body.auto<Credentials>().toLens()

            val response = app(
                Request(POST, "/reader/authenticate")
                    .body(requestLens(authRequest, Response(ACCEPTED)).bodyString())
            )

            response.status shouldBe OK
            println(response)
            token = tokenLens(response).token
        }

        should("get OK from user endpoint") {
            val response = app(Request(GET, "/reader/${reader?.id}").header("Authorization", "Bearer $token"))
            response.status shouldBe OK
            val readerResponse = readerLens(response)
            readerResponse.firstName shouldBe "Joe"
            readerResponse.lastName shouldBe "Bloggs"
            readerResponse.email shouldBe "joe.bloggs@mildlyskilled.com"
        }

        should("return UNAUTHORIZED where the user does not own this reader account") {
            val messageLens = Body.auto<Message>().toLens()
            val response =
                app(Request(GET, "/reader/${UUID.randomUUID()}").header("Authorization", "Bearer $token"))
            response.status shouldBe UNAUTHORIZED
            response.bodyString() shouldBe messageLens(
                Message("You are not allowed to access this reader data"),
                Response(NOT_FOUND)
            ).bodyString()
        }

        should("get OK from the feed endpoint") {
            val response = app(Request(GET, "/feed/${reader?.id}").header("Authorization", "Bearer $token"))
            response.status shouldBe OK
        }

        should("get not found if a non existent id is passed") {
            app(
                Request(GET, "/feed/${UUID.randomUUID()}").header(
                    "Authorization",
                    "Bearer $token"
                )
            ).status shouldBe NOT_FOUND
        }

        should("accept uploaded feeds") {
            val sample = ReaderTest::class.java.getResource("/xml/my_rss_feeds.opml")?.readText()
            val base64 = Base64.getEncoder().encodeToString(sample?.toByteArray())
            val importRequest = ImportRequest(
                readerId = reader!!.id.toString(),
                payload = base64
            )

            val requestLens = Body.auto<ImportRequest>().toLens()
            app(
                Request(POST, "/feed/import")
                    .header("Authorization", "Bearer $token")
                    .body(requestLens(importRequest, Response(ACCEPTED)).bodyString())
            ).status shouldBe ACCEPTED
        }

        should("get imported feed") {
            val response = app(Request(GET, "/feed/${reader?.id}").header("Authorization", "Bearer $token"))
            response.status shouldBe OK
            val userFeed = userFeedLens(response)
            userFeed.name shouldBe "Joe Bloggs"
            userFeed.sections shouldHaveSize 11
            userFeed.sections.flatMap { it.feeds } shouldHaveSize 42
            newsSectionId = userFeed.sections.first { it.name.toLowerCase() == "news" }.id
        }

        should("fetch section feeds") {
            val response = app(
                Request(GET, "/feed/section/${newsSectionId!!}")
                    .header("Authorization", "Bearer $token")
            )
            response.status shouldBe OK
            val sectionNews = sectionLens(response)
            sectionNews.feeds shouldHaveSize 2
        }
    }
})
