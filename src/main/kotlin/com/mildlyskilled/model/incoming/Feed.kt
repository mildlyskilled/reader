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

interface NewsFeed

data class Rss(val channel: Channel) : NewsFeed {
    val size = channel.item.size
}

data class Author(val name: String?, val email: String?, val uri: String?)
data class Link(val rel: String, val type: String?, val href: String)
data class Entry(
    val id: String,
    val title: String,
    val summary: String,
    val link: Link,
    val author: Author?,
    val updated: String
)

data class Atom(
    val id: String,
    val title: String,
    val rights: String?,
    val icon: String?,
    val subTitle: String?,
    val logo: String?,
    val updated: String,
    val author: Author?,
    val entry: List<Entry>
) : NewsFeed

data class Image(val url: String, val title: String, val link: String)
data class Channel(
    val title: String,
    val description: String,
    val link: String,
    val image: Image,
    val generator: String?,
    val lastBuildDate: String?,
    val language: String,
    val ttl: Int,
    val item: List<Item>
)

data class Item(
    val title: String,
    val description: String,
    val link: String,
    val pubDate: String
)

data class ImportRequest(
    val readerId: String,
    val payload: String
)