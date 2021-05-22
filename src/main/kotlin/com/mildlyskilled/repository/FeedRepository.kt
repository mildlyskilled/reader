package com.mildlyskilled.repository

import com.mildlyskilled.model.entity.Feed
import com.mildlyskilled.model.entity.NewsItem
import com.mildlyskilled.model.entity.Section
import com.mildlyskilled.model.incoming.NewsFeed
import com.mildlyskilled.model.incoming.Opml
import com.mildlyskilled.model.incoming.Rss
import com.mildlyskilled.model.outgoing.UserFeed
import java.util.UUID


interface FeedRepository {
    suspend fun getReaderSections(readerId: UUID): UserFeed?
    suspend fun persistSections(readerId: UUID, opml: Opml): List<Unit?>
    suspend fun getFeedById(feedId: UUID): Feed?
    suspend fun getSectionById(sectionId: UUID): Section?
    suspend fun getFeedNews(feedId: UUID): List<NewsItem>
    suspend fun persistNews(feedId: UUID, newsFeed: NewsFeed): UUID?
}