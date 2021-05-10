package com.mildlyskilled.service

import com.mildlyskilled.listener.ServiceTestListener
import com.mildlyskilled.model.DbConfiguration
import com.mildlyskilled.model.incoming.NewReaderRequest
import com.mildlyskilled.model.entity.Reader
import com.mildlyskilled.parser.Parser
import com.mildlyskilled.parser.ParserTest
import com.mildlyskilled.repository.db.DbFeedRepository
import com.mildlyskilled.repository.db.DbReaderRepository
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import io.kotlintest.extensions.TestListener
import io.kotlintest.specs.ShouldSpec
import kotlinx.coroutines.runBlocking
import org.http4k.core.Method
import org.http4k.core.Request
import org.junit.jupiter.api.Assertions.assertEquals


class FeedServiceTest : ShouldSpec() {
    override fun listeners(): List<TestListener> = listOf(ServiceTestListener)
    private val config = ConfigFactory.load()
    private val dbConfig: DbConfiguration = config.extract("db")
    private val feedRepository = DbFeedRepository(dbConfig)
    private val feedService = FeedService(feedRepository)
    private val readerRepository = DbReaderRepository(dbConfig)
    private val readerService = ReaderService(readerRepository)
    private val newReaderRequest = NewReaderRequest(
        firstName = "Joe",
        lastName = "Bloggs",
        email = "joe.bloggs@mildlyskilled.com",
        password = "test"
    )

    private var reader: Reader? = null

    init {
        "Feed Service" {
            runBlocking {
                reader = readerService.newReader(newReaderRequest)
            }

            should("persist imported feed") {
                val sample = ParserTest::class.java.getResource("/xml/my_rss_feeds.opml")?.readText()
                val feed = Parser.parseOpml(Request(Method.GET, "/").body(sample!!))
                assert(runBlocking { feedService.saveFeed(reader?.id.toString(), feed) })
            }

            should("get user news feed") {
                assertEquals(11, runBlocking { feedService.readerFeed(reader?.id.toString()) }?.sections?.size)
            }

            should("persist individual feed") {
                val userFeed = runBlocking { feedService.readerFeed(reader?.id.toString()) }
                assertEquals(11, userFeed?.sections?.size)
            }
        }
    }
}