package com.mildlyskilled.repository

import com.mildlyskilled.model.incoming.Opml
import com.mildlyskilled.model.outgoing.UserFeed
import java.util.UUID


interface FeedRepository {
    suspend fun getUserSections(readerId: UUID): UserFeed?
    suspend fun persistFeed(readerId: UUID, opml: Opml): List<Unit?>
}