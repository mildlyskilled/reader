package com.mildlyskilled.model.entity

import com.mildlyskilled.repository.db.FeedTable
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class Feed(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : EntityClass<UUID, Feed>(FeedTable)
    var name by FeedTable.name
    var title by FeedTable.title
    var type by FeedTable.type
    var xmlUrl by FeedTable.xmlUrl
    var htmlUrl by FeedTable.htmlUrl
    var icon by FeedTable.icon
}