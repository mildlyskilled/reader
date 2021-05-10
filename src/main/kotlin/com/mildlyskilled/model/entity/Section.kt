package com.mildlyskilled.model.entity

import com.mildlyskilled.repository.db.SectionFeedTable
import com.mildlyskilled.repository.db.SectionTable
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class Section(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : EntityClass<UUID, Section>(SectionTable)
    var name by SectionTable.name
    var title by SectionTable.title
    var owner by SectionTable.reader
    var created by SectionTable.created
    var updated by SectionTable.updated
    var feeds by Feed via SectionFeedTable
}