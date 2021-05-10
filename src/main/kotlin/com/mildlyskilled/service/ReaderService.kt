package com.mildlyskilled.service

import com.mildlyskilled.model.incoming.NewReaderRequest
import com.mildlyskilled.repository.ReaderRepository
import java.util.UUID

class ReaderService(private val readerRepository: ReaderRepository) {

    suspend fun newReader(newReaderRequest: NewReaderRequest) =
        readerRepository.storeReader(newReaderRequest.copy(password = "obfuscated"))

    suspend fun getReader(userId: String) =
        readerRepository.getReader(UUID.fromString(userId))

}