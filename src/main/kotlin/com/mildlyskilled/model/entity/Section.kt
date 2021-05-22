package com.mildlyskilled.model.entity

import com.mildlyskilled.repository.db.SectionFeedTable
import com.mildlyskilled.repository.db.SectionTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID
import com.mildlyskilled.model.outgoing.Section as OutgoingSection

class Section(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Section>(SectionTable)

    var name by SectionTable.name
    var title by SectionTable.title
    var owner by SectionTable.reader
    var created by SectionTable.created
    var updated by SectionTable.updated
    var feeds by Feed via SectionFeedTable

    fun toOutgoingSection(): OutgoingSection {
        val section = this
        return OutgoingSection(
            id = section.id.value,
            name = section.name,
            title = section.title,
            owner = section.owner.value,
            created = section.created.toString(),
            updated = section.updated?.toString(),
            feeds = transaction {
                section.feeds.map {
                    it.toOutGoingNewsFeed()
                }
            }
        )
    }
}
