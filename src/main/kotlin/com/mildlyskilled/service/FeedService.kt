package com.mildlyskilled.service

import com.mildlyskilled.model.incoming.Opml
import com.mildlyskilled.repository.FeedRepository
import java.util.UUID

class FeedService(private val feedRepository: FeedRepository) {
    suspend fun readerFeed(readerId: String) =
        feedRepository.getUserSections(UUID.fromString(readerId))

    suspend fun saveFeed(readerId: String, opml: Opml): Boolean =
        feedRepository.persistFeed(UUID.fromString(readerId), opml).isNotEmpty()
}