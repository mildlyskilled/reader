package com.mildlyskilled.parser

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import org.http4k.core.Body
import org.http4k.core.MemoryResponse
import org.http4k.core.Status
import org.junit.jupiter.api.Assertions.assertEquals

class ParserTest : ShouldSpec({
    context("Feed parser") {
        should("extract models from OPML Feed") {
            val sample = ParserTest::class.java.getResource("/xml/my_rss_feeds.opml")?.readText()
            val feed = Parser.parseOpml(MemoryResponse(body = Body(sample!!), status = Status.OK))
            feed.body.outline.forEach {
                when (it.text) {
                    "News" -> assertEquals(2, it.outline?.size, message(it.text))
                    "Others" -> assertEquals(1, it.outline?.size, message(it.text))
                    "technology" -> assertEquals(12, it.outline?.size, message(it.text))
                    "Comics" -> assertEquals(1, it.outline?.size, message(it.text))
                    "Funny" -> assertEquals(7, it.outline?.size, message(it.text))
                    "OSS" -> assertEquals(5, it.outline?.size, message(it.text))
                    "Ubuntu" -> assertEquals(2, it.outline?.size, message(it.text))
                    "Geeky Stuff" -> assertEquals(8, it.outline?.size, message(it.text))
                    "tech" -> assertEquals(3, it.outline?.size, message(it.text))
                    "Thinkers" -> assertEquals(1, it.outline?.size, message(it.text))
                }
            }
        }
        should("extract models from RSS") {
            val sample = ParserTest::class.java.getResource("/xml/feeds/bbc.xml")?.readText()
            val feed = Parser.parseRss(MemoryResponse(body = Body(sample!!), status = Status.OK))
            assertEquals("BBC News - Home", feed.channel.title)
            assertEquals(47, feed.size)
        }

        should("parse atom feed") {
            val sample = ParserTest::class.java.getResource("/xml/feeds/register.atom.xml")?.readText()
            val response = MemoryResponse(body = Body(sample!!), status = Status.OK)
            val feed = Parser.parseAtom(response)
            feed.entry shouldHaveSize 50
        }
    }
})

private fun message(title: String) = "Wrong feed count for $title"