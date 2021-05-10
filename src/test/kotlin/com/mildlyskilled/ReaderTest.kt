package com.mildlyskilled

import com.mildlyskilled.listener.ServiceTestListener
import com.mildlyskilled.model.outgoing.Reader as MildlySkilledReader
import com.mildlyskilled.model.incoming.NewReaderRequest
import com.mildlyskilled.model.outgoing.Message
import io.kotlintest.extensions.TestListener
import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.ACCEPTED
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson.auto
import org.http4k.kotest.shouldHaveBody
import org.http4k.kotest.shouldHaveStatus
import java.util.UUID

class ReaderTest : ShouldSpec() {
    override fun listeners(): List<TestListener> = listOf(ServiceTestListener)

    private val newReaderRequest = NewReaderRequest(
        firstName = "Joe",
        lastName = "Bloggs",
        email = "joe.bloggs@mildlyskilled.com",
        password = "test"
    )

    private var reader: MildlySkilledReader? = null

    init {
        "Reader Application" {
            should("register a new reader") {
                val requestLens = Body.auto<NewReaderRequest>().toLens()
                val readerLens = Body.auto<MildlySkilledReader>().toLens()
                val newReaderResponse = app(Request(POST, "/reader/new").body(requestLens(newReaderRequest, Response(ACCEPTED)).bodyString()))
                reader = readerLens(newReaderResponse)
                reader?.firstName shouldBe "Joe"
                reader?.lastName shouldBe "Bloggs"
                reader?.email shouldBe "joe.bloggs@mildlyskilled.com"
            }

            should("get OK from user endpoint") {
                app(Request(GET, "/reader/${reader?.id}")) shouldHaveStatus OK
                val message = Body.auto<MildlySkilledReader>().toLens()
                val reader = message(app(Request(GET, "/reader/${reader?.id}")))
                reader.firstName shouldBe "Joe"
                reader.lastName shouldBe "Bloggs"
                reader.email shouldBe "joe.bloggs@mildlyskilled.com"
            }

            should("return NOT_FOUND where we don't have a user") {
                val messageLens = Body.auto<Message>().toLens()
                app(Request(GET, "/reader/${UUID.randomUUID()}")) shouldHaveStatus NOT_FOUND
                app(Request(GET, "/reader/${UUID.randomUUID()}")) shouldHaveBody messageLens(Message("user not found"), Response(
                    NOT_FOUND)).bodyString()
            }

            should("get OK from the feed endpoint") {
                app(Request(GET, "/feed/${reader?.id}")) shouldHaveStatus OK
            }

            should("get not found if a non existent id is passed") {
                app(Request(GET, "/feed/${UUID.randomUUID()}")) shouldHaveStatus NOT_FOUND
            }
        }
    }
}
