package com.mildlyskilled.repository

import com.mildlyskilled.model.entity.Reader
import com.mildlyskilled.model.incoming.NewReaderRequest
import java.util.UUID

interface ReaderRepository {
    suspend fun storeReader(reader: NewReaderRequest): Reader
    suspend fun getReader(readerId: UUID): Reader?
    suspend fun getReaderByEmail(email: String): Reader?
}