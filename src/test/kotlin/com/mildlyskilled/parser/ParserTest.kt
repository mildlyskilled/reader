package com.mildlyskilled.parser

import org.http4k.core.Method
import org.http4k.core.Request
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ParserTest {
    @Test
    fun `Should extract models from OPML Feed`() {
        val sample = ParserTest::class.java.getResource("/xml/my_rss_feeds.opml")?.readText()
        val feed = Parser.parseOpml(Request(Method.GET, "/").body(sample!!))
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

    @Test
    fun `Should extract models from RSS`() {
        val sample = ParserTest::class.java.getResource("/xml/sample_news_feed.xml")?.readText()
        val feed = Parser.parseRss(Request(Method.GET, "/").body(sample!!))
        assertEquals("BBC News - Home", feed.channel.title)
        assertEquals(47, feed.size)
    }

    private fun message(title: String) = "Wrong feed count for $title"
}