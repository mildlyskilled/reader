package com.mildlyskilled

import com.mildlyskilled.model.DbConfiguration
import com.mildlyskilled.repository.db.DbFeedRepository
import com.mildlyskilled.repository.db.DbReaderRepository
import com.mildlyskilled.repository.db.createDb
import com.mildlyskilled.routes.feed
import com.mildlyskilled.routes.reader
import com.mildlyskilled.service.FeedService
import com.mildlyskilled.service.ReaderService
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.filter.DebuggingFilters.PrintResponse
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer


val config: Config = ConfigFactory.load()
val dbConfig = config.extract<DbConfiguration>("db")
val feedRepository = DbFeedRepository(dbConfig)
val feedService = FeedService(feedRepository)
val readerRepository = DbReaderRepository(dbConfig)
val readerService = ReaderService(readerRepository)


val app = routes(*(feed(feedService) + reader(readerService)))



fun main() {
    createDb()

    val server = PrintRequest()
        .then(PrintResponse())
        .then(app)
        .asServer(SunHttp(9000)).start()

    println("Server started on ${server.port()}")
}
