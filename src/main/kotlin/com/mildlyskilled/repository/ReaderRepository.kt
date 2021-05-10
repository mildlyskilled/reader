package com.mildlyskilled.repository

import com.mildlyskilled.model.entity.Reader
import com.mildlyskilled.model.incoming.NewReaderRequest
import java.util.UUID
import com.mildlyskilled.model.outgoing.Reader as MildlySkilledReader

interface ReaderRepository {
    suspend fun storeReader(reader: NewReaderRequest): Reader
    suspend fun getReader(readerId: UUID): Reader?
}