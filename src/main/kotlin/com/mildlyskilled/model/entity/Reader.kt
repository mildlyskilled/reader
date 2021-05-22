package com.mildlyskilled.model.entity

import com.mildlyskilled.repository.db.ReaderTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID
import com.mildlyskilled.model.outgoing.Reader as MildlySkilledReader

class Reader(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Reader>(ReaderTable)

    var avatar by ReaderTable.avatar
    var firstName by ReaderTable.firstName
    var lastName by ReaderTable.lastName
    var email by ReaderTable.email
    var password by ReaderTable.password
    var created by ReaderTable.created
    var updated by ReaderTable.updated
    var deleted by ReaderTable.deleted

    fun toOutgoing() = with(this) {
        MildlySkilledReader(
            id = this.id.value,
            firstName = this.firstName,
            lastName = this.lastName,
            email = this.email,
            avatar = this.avatar,
            created = this.created.toString()
        )
    }

}