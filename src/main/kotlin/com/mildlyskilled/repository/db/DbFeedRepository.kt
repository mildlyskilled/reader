package com.mildlyskilled.repository.db

import com.mildlyskilled.model.common.DbConfiguration
import com.mildlyskilled.model.entity.Feed
import com.mildlyskilled.model.entity.Icon
import com.mildlyskilled.model.entity.NewsItem
import com.mildlyskilled.model.entity.Reader
import com.mildlyskilled.model.entity.Section
import com.mildlyskilled.model.incoming.Atom
import com.mildlyskilled.model.incoming.NewsFeed
import com.mildlyskilled.model.incoming.Opml
import com.mildlyskilled.model.incoming.Rss
import com.mildlyskilled.model.outgoing.UserFeed
import com.mildlyskilled.repository.FeedRepository
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.util.UUID

class DbFeedRepository(config: DbConfiguration) : FeedRepository, JdbcRepository(config) {
    override suspend fun getReaderSections(readerId: UUID): UserFeed? =
        transaction {
            Reader.findById(readerId)?.let { reader ->
                UserFeed(
                    name = "${reader.firstName} ${reader.lastName}",
                    Section.find { SectionTable.reader eq readerId }.toList().map { it.toOutgoingSection() })
            }
        }

    override suspend fun persistSections(readerId: UUID, opml: Opml) =
        opml.body.outline.map { outline ->
            val section = transaction {
                Reader.findById(readerId)?.let { reader ->
                    Section.new(UUID.randomUUID()) {
                        name = outline.text
                        title = outline.title
                        owner = reader.id
                        created = DateTime.now()
                    }
                }
            }

            val feeds = outline.outline?.let { feeds ->
                feeds.map { feed ->
                    val optionalIcon = feed.icon?.let { feedIcon ->
                        transaction {
                            Icon.new(UUID.randomUUID()) {
                                title = feed.title
                                link = feedIcon
                            }
                        }
                    }

                    transaction {
                        Feed.new(UUID.randomUUID()) {
                            name = feed.text
                            title = feed.title
                            feed.type?.let { feedType ->
                                type = feedType
                            }
                            feed.xmlUrl?.let { feedXmlUrl ->
                                xmlUrl = feedXmlUrl
                            }
                            feed.htmlUrl?.let { feedHtmlUrl ->
                                htmlUrl = feedHtmlUrl
                            }
                            icon = optionalIcon?.id
                        }
                    }

                }
            }

            feeds?.let {
                transaction {
                    section?.feeds = SizedCollection(it)
                }
            }
        }

    override suspend fun getSectionById(sectionId: UUID): Section? =
        transaction { Section.findById(sectionId) }

    override suspend fun getFeedById(feedId: UUID): Feed? =
        transaction { Feed.findById(feedId) }

    override suspend fun getFeedNews(feedId: UUID): List<NewsItem> =
        transaction { NewsItem.find { NewsTable.feed eq feedId }.toList() }

    override suspend fun persistNews(feedId: UUID, newsFeed: NewsFeed): UUID? =
        transaction {
            Feed.find { FeedTable.id eq feedId }.firstOrNull()?.let { channel ->
                when(newsFeed){
                    is Rss -> newsFeed.channel.item.forEach { newsItem ->
                        NewsItem.new(UUID.randomUUID()) {
                            title = newsItem.title
                            description = newsItem.description
                            link = newsItem.link
                            publishedAt = newsItem.pubDate
                            readAt = null
                            feed = channel.id
                        }
                    }
                    is Atom -> newsFeed.entry.forEach{ newsItem ->
                        NewsItem.new(UUID.randomUUID()) {
                            title = newsItem.title
                            description = newsItem.summary
                            link = newsItem.link.href
                            publishedAt = newsItem.updated
                            readAt = null
                            feed = channel.id
                        }
                    }
                }


                channel.id.value
            }
        }
}
