package com.mildlyskilled.model.outgoing

import com.mildlyskilled.model.entity.Section


data class Guid(val isPermalink: Boolean)

data class NewsFeed(
    val title: String,
    val description: String,
    val link: String,
    val guid: Guid,
    val pubDate: String
)


data class UserFeed(
    val name: String,
    val sections: List<Section>
)
