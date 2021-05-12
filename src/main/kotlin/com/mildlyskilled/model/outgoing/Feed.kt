package com.mildlyskilled.model.outgoing

import org.joda.time.DateTime
import java.util.UUID


data class Guid(val isPermalink: Boolean)

data class NewsItem(
    val title: String,
    val description: String,
    val link: String,
    val guid: Guid,
    val pubDate: String
)

data class Feed(
    val name: String,
    val title: String,
    val type: String,
    val xmlUrl: String?,
    val htmlUrl: String?,
    val icon: UUID?,
)

data class Section(
    val name: String,
    val title: String,
    val owner: UUID,
    val created: String,
    val updated: String?,
    val feeds: List<Feed>
)

data class UserFeed(
    val name: String,
    val sections: List<Section>
)
