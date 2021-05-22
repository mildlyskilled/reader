package com.mildlyskilled.repository.db

import com.mildlyskilled.model.common.DbConfiguration
import com.mildlyskilled.model.entity.Reader
import com.mildlyskilled.model.incoming.NewReaderRequest
import com.mildlyskilled.repository.ReaderRepository
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.util.UUID

class DbReaderRepository(config: DbConfiguration) : JdbcRepository(config),
    ReaderRepository {
    override suspend fun storeReader(reader: NewReaderRequest): Reader =
        transaction {
            addLogger(StdOutSqlLogger)
            Reader.new(UUID.randomUUID()) {
                firstName = reader.firstName
                lastName = reader.lastName
                email = reader.email
                password = reader.password
                created = DateTime.now()
            }
        }

    override suspend fun getReader(readerId: UUID): Reader? =
        transaction {
            Reader.findById(readerId)
        }

    override suspend fun getReaderByEmail(email: String): Reader? =
        transaction {
            addLogger(StdOutSqlLogger)
            Reader.find { (ReaderTable.email eq email) }.firstOrNull()
        }
}