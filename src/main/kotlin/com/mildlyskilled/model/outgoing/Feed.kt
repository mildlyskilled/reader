package com.mildlyskilled.model.outgoing

import java.util.UUID


data class NewsItem(
    val title: String,
    val description: String,
    val link: String,
    val pubDate: String,
    val section: UUID,
    val readAt: String?
)

data class Feed(
    val id: UUID,
    val name: String,
    val title: String,
    val type: String,
    val xmlUrl: String?,
    val htmlUrl: String?,
    val icon: UUID?,
)

data class Section(
    val id: UUID,
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
