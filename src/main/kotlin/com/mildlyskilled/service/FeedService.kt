package com.mildlyskilled.service

import arrow.core.Either
import com.mildlyskilled.model.common.MildlySkilledException
import com.mildlyskilled.model.common.Result
import com.mildlyskilled.model.incoming.NewsFeed
import com.mildlyskilled.model.incoming.Opml
import com.mildlyskilled.model.incoming.Rss
import com.mildlyskilled.parser.Parser
import com.mildlyskilled.repository.FeedRepository
import mu.KotlinLogging
import okhttp3.OkHttpClient
import okhttp3.Request
import org.http4k.core.Body
import org.http4k.core.MemoryResponse
import org.http4k.core.Status
import java.util.UUID
import okhttp3.Response as OkHttpResponse

class FeedService(private val feedRepository: FeedRepository, private val client: OkHttpClient) {
    private val logger = KotlinLogging.logger {}
    suspend fun readerFeed(readerId: String) =
        feedRepository.getReaderSections(UUID.fromString(readerId))

    suspend fun saveFeed(readerId: String, opml: Opml): Boolean =
        feedRepository.persistSections(UUID.fromString(readerId), opml).isNotEmpty()

    suspend fun saveFeed(feedId: String, feed: NewsFeed): UUID? =
        feedRepository.persistNews(UUID.fromString(feedId), feed)

    suspend fun newsByFeed(feedId: String) =
        feedRepository.getFeedNews(UUID.fromString(feedId)).map { it.toOutgoingNews() }

    suspend fun getFeed(feedId: String) =
        Result.build {
            feedRepository.getFeedById(UUID.fromString(feedId))?.toOutGoingNewsFeed()
                ?: throw MildlySkilledException("No feed were found")
        }


    suspend fun getSection(sectionId: String) =
        Result.build {
            feedRepository.getSectionById(UUID.fromString(sectionId))?.toOutgoingSection()
                ?: throw MildlySkilledException("No sections were found")
        }

    suspend fun fetchSectionNewsFeeds(sectionId: String): Either<Exception, List<NewsFeed>> =
        Result.build {
            feedRepository.getSectionById(UUID.fromString(sectionId))?.toOutgoingSection()?.feeds?.map { feed ->
                logger.info("Fetching ${feed.xmlUrl}")
                feed.xmlUrl?.let { url ->
                    val request = Request.Builder().url(url).build()
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        when (feed.type.toLowerCase()) {
                            "rss" -> toHttp4kResponse(response)?.let { Parser.parseRss(it) }
                            "atom" -> toHttp4kResponse(response)?.let { Parser.parseAtom(it) }
                            else -> throw MildlySkilledException("Unsupported feed format")
                        }
                    } else {
                        throw MildlySkilledException(response.message)
                    }
                }
            }?.filterNotNull() ?: emptyList()
        }


    private fun toHttp4kResponse(okHttpResponse: OkHttpResponse) =
        okHttpResponse.body?.string()?.let { response ->
            MemoryResponse(body = Body(response), status = Status.OK)
        }

}

