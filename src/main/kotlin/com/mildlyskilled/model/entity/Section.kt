package com.mildlyskilled.model.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.mildlyskilled.repository.db.SectionFeedTable
import com.mildlyskilled.repository.db.SectionTable
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID
import com.mildlyskilled.model.outgoing.Section as OutgoingSection

class Section(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : EntityClass<UUID, Section>(SectionTable)
    var name by SectionTable.name
    var title by SectionTable.title
    var owner by SectionTable.reader
    var created by SectionTable.created
    var updated by SectionTable.updated
    var feeds by Feed via SectionFeedTable

    fun toOutgoingSection() =
        with(this){
            OutgoingSection(
                name = this.name,
                title = this.title,
                owner = this.owner.value,
                created = this.created.toString(),
                updated = this.updated?.toString(),
                feeds = this.feeds.map {
                    it.toOutGoingNewsFeed()
                }
            )
        }
}