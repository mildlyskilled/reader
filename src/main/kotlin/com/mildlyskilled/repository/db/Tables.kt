package com.mildlyskilled.repository.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.jodatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction


object ReaderTable : UUIDTable("READER") {
    val avatar = text("AVATAR").nullable()
    val firstName = varchar("FIRST_NAME", length = 50)
    val lastName = varchar("LAST_NAME", length = 50)
    val email = varchar("EMAIL", length = 256).uniqueIndex()
    val password = varchar("PASSWORD", length = 256)
    val created = datetime("CREATED_AT")
    val updated = datetime("UPDATED_AT").nullable()
    val deleted = datetime("DELETED_AT").nullable()
}

object SectionTable : UUIDTable("NEWS_SECTION") {
    val name = varchar("SECTION_NAME", length = 50)
    val title = text("SECTION_TITLE")
    val reader = reference("reader", ReaderTable)
    val created = datetime("CREATED_AT")
    val updated = datetime("UPDATED_AT").nullable()
}

object SectionFeedTable : IntIdTable("SECTION_FEED") {
    val section = reference("NEWS_SECTION", SectionTable)
    val feed = reference("FEED", FeedTable)
}

object IconTable : UUIDTable("ICON") {
    val title = text("ICON_TITLE")
    val link = text("ICON_LINK").nullable()
}

object FeedTable : UUIDTable("FEED") {
    val name = text("FEED_NAME")
    val title = text("FEED_TITLE")
    val type = varchar("FEED_TYPE", length = 5)
    val xmlUrl = text("XML_URL").nullable()
    val htmlUrl = text("HTML_URL").nullable()
    val icon = reference("ICON", IconTable).nullable()
}

object NewsTable : UUIDTable("NEWS") {
    val title = text("NEWS_TITLE")
    val description = text("NEWS_DESCRIPTION")
    val link = text("LINK")
    val guid = text("GUID")
    val isPermalink = bool("IS_PERMALINK")
    val publishedAt = datetime("PUBLISHED_AT")
    val readAt = datetime("READ_AT")
    val feed = reference("FEED", FeedTable)
}

fun createDb() {
    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(ReaderTable)
        SchemaUtils.create(SectionTable)
        SchemaUtils.create(FeedTable)
        SchemaUtils.create(SectionFeedTable)
        SchemaUtils.create(IconTable)
        SchemaUtils.create(NewsTable)
    }
}

fun destroyDb() {
    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.drop(ReaderTable)
        SchemaUtils.drop(SectionTable)
        SchemaUtils.drop(FeedTable)
        SchemaUtils.drop(SectionFeedTable)
        SchemaUtils.drop(IconTable)
        SchemaUtils.drop(NewsTable)
    }
}
