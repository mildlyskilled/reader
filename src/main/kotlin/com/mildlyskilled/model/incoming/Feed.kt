package com.mildlyskilled.model.incoming

data class Opml(val head: Head, val body: Body)
data class Head(val title: String)
data class Body(val outline: List<Outline>)
data class Outline(
    val text: String,
    val title: String,
    val type: String?,
    val xmlUrl: String?,
    val htmlUrl: String?,
    val icon: String?,
    val outline: List<Outline>?
)

data class Rss(val channel: Channel) {
    val size = channel.item.size
}
data class Image(val url: String, val title: String, val link: String)
data class Guid(val isPermalink: Boolean)
data class Channel(
    val title: String,
    val description: String,
    val link: String,
    val image: Image,
    val generator: String?,
    val lastBuildDate: String,
    val language: String,
    val ttl: Int,
    val item: List<Item>
)

data class Item(
    val title: String,
    val description: String,
    val link: String,
    val guid: Guid,
    val pubDate: String
)

data class ImportRequest(
    val readerId: String,
    val payload: String
)