package com.mildlyskilled.model.entity

import com.mildlyskilled.repository.db.NewsTable
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID
import com.mildlyskilled.model.outgoing.NewsItem as OutgoingNewsItem

class NewsItem(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : EntityClass<UUID, NewsItem>(NewsTable)

    var title by NewsTable.title
    var description by NewsTable.description
    var link by NewsTable.link
    var publishedAt by NewsTable.publishedAt
    var readAt by NewsTable.readAt
    var feed by NewsTable.feed

    fun toOutgoingNews() = with(this) {
        OutgoingNewsItem(
            title = this.title,
            description = this.description,
            link = this.link,
            readAt = this.readAt,
            pubDate = this.publishedAt,
            section = this.feed.value
        )
    }

}