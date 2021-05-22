package com.mildlyskilled.model.entity

import com.mildlyskilled.repository.db.FeedTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID
import com.mildlyskilled.model.outgoing.Feed as OutgoingFeed

class Feed(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Feed>(FeedTable)

    var name by FeedTable.name
    var title by FeedTable.title
    var type by FeedTable.type
    var xmlUrl by FeedTable.xmlUrl
    var htmlUrl by FeedTable.htmlUrl
    var icon by FeedTable.icon

    fun toOutGoingNewsFeed() = with(this) {
        OutgoingFeed(
            id = this.id.value,
            name = this.name,
            title = this.title,
            type = this.type,
            xmlUrl = this.xmlUrl,
            htmlUrl = this.htmlUrl,
            icon = this.icon?.value
        )
    }
}