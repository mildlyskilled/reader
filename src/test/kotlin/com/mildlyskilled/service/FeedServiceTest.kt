package com.mildlyskilled.service

import com.mildlyskilled.listener.ServiceTestListener
import com.mildlyskilled.model.common.DbConfiguration
import com.mildlyskilled.model.entity.Reader
import com.mildlyskilled.model.incoming.NewReaderRequest
import com.mildlyskilled.parser.Parser
import com.mildlyskilled.parser.ParserTest
import com.mildlyskilled.repository.db.DbFeedRepository
import com.mildlyskilled.repository.db.DbReaderRepository
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import io.kotest.assertions.fail
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.http4k.core.Body
import org.http4k.core.MemoryResponse
import org.http4k.core.Status
import org.junit.jupiter.api.Assertions.assertEquals


class FeedServiceTest : ShouldSpec({
    listener(ServiceTestListener)

    val config = ConfigFactory.load()
    val dbConfig: DbConfiguration = config.extract("db")
    val feedRepository = DbFeedRepository(dbConfig)
    val feedService = FeedService(feedRepository, OkHttpClient())
    val readerRepository = DbReaderRepository(dbConfig)
    val readerService = ReaderService(readerRepository)
    val newReaderRequest = NewReaderRequest(
        firstName = "Joe",
        lastName = "Bloggs",
        email = "joe.bloggs@mildlyskilled.com",
        password = "test"
    )

    var bbcFeedId: String? = null
    var registerFeedId: String? = null
    var newsSectionId: String? = null

    context("Feed Service ") {

        val reader = readerService.newReader(newReaderRequest)

        should("persist imported feed") {
            val sample = ParserTest::class.java.getResource("/xml/my_rss_feeds.opml")?.readText()
            val feed = Parser.parseOpml(MemoryResponse(body = Body(sample!!), status = Status.OK))
            assert(feedService.saveFeed(reader.id.toString(), feed))
        }

        should("get user news feed") {
            val readerFeed = feedService.readerFeed(reader.id.toString())
            assertEquals(11, readerFeed?.sections?.size)
            bbcFeedId = readerFeed?.sections?.flatMap {
                it.feeds.map { feed -> Pair(feed.name, feed.id) }
            }?.first { it.first.toLowerCase() == "bbc" }?.second.toString()

            registerFeedId = readerFeed?.sections?.flatMap {
                it.feeds.map { feed -> Pair(feed.name, feed.id) }
            }?.first { it.first.toLowerCase() == "the register" }?.second.toString()

            newsSectionId = readerFeed?.sections?.first { it.name == "News" }?.id.toString()
        }

        should("persist individual feed") {
            val userFeed = feedService.readerFeed(reader.id.toString())
            assertEquals(11, userFeed?.sections?.size)
        }

        should("persist rss feed") {
            val sample = ParserTest::class.java.getResource("/xml/feeds/bbc.xml")?.readText()
            val rss = Parser.parseRss(MemoryResponse(body = Body(sample!!), status = Status.OK))
            feedService.saveFeed(bbcFeedId.toString(), rss)?.let { feedId ->
                val newsItems = feedService.newsByFeed(feedId.toString())
                assertEquals(47, newsItems.size)
            } ?: fail("Could not save feed")
        }

        should("retrieve news by section id") {
            val newsItems = feedService.newsByFeed(bbcFeedId!!)
            assertEquals(47, newsItems.size)
        }

        should("persist atom feed"){
            val sample = ParserTest::class.java.getResource("/xml/feeds/register.atom.xml")?.readText()
            val atom = Parser.parseAtom(MemoryResponse(body = Body(sample!!), status = Status.OK))
            feedService.saveFeed(registerFeedId.toString(), atom)?.let { feedId ->
                val newsItems = feedService.newsByFeed(feedId.toString())
                assertEquals(50, newsItems.size)
            } ?: fail("Could not save feed")
        }

        should("retrieve news by section id") {
            val newsItems = feedService.newsByFeed(registerFeedId!!)
            assertEquals(50, newsItems.size)
        }

        should("get section given an id") {
            val sectionData = feedService.getSection(newsSectionId!!)
            sectionData.map {
                it.feeds shouldHaveSize 2
            }
        }

        should("fetch news from feed sections") {
            feedService.fetchSectionNewsFeeds(newsSectionId!!).map { response ->
                println(response)
            }
        }
    }
})