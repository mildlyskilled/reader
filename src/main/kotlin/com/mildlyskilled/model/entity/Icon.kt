package com.mildlyskilled.model.entity

import com.mildlyskilled.repository.db.IconTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class Icon(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Icon>(IconTable)

    var title by IconTable.title
    var link by IconTable.link
}