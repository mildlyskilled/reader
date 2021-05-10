package com.mildlyskilled.repository.db

import com.mildlyskilled.model.DbConfiguration
import com.mildlyskilled.model.entity.Feed
import com.mildlyskilled.model.entity.Icon
import com.mildlyskilled.model.entity.Reader
import com.mildlyskilled.model.entity.Section
import com.mildlyskilled.model.incoming.Opml
import com.mildlyskilled.model.outgoing.UserFeed
import com.mildlyskilled.repository.FeedRepository
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.util.UUID

class DbFeedRepository(config: DbConfiguration) : FeedRepository, JdbcRepository(config) {
    override suspend fun getUserSections(readerId: UUID): UserFeed? =
        transaction {
            Reader.findById(readerId)?.let { reader ->
                UserFeed(name = "${reader.firstName} ${reader.lastName}" ,Section.find { SectionTable.reader eq readerId }.toList())
            }
        }

    override suspend fun persistFeed(userId: UUID, opml: Opml) =
        opml.body.outline.map { outline ->
            val section = transaction {
                Reader.findById(userId)?.let { reader ->
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
}
