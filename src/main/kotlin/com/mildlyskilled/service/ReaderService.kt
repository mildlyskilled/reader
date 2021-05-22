package com.mildlyskilled.service

import com.lambdaworks.crypto.SCryptUtil
import com.mildlyskilled.model.incoming.NewReaderRequest
import com.mildlyskilled.repository.ReaderRepository
import java.util.UUID

class ReaderService(private val readerRepository: ReaderRepository) {

    suspend fun newReader(newReaderRequest: NewReaderRequest) =
        readerRepository.storeReader(
            newReaderRequest.copy(
                password = SCryptUtil.scrypt(
                    newReaderRequest.password,
                    16384,
                    8,
                    1
                )
            )
        )

    suspend fun readerById(userId: String) =
        readerRepository.getReader(UUID.fromString(userId))

    suspend fun readerByEmail(email: String) =
        readerRepository.getReaderByEmail(email)
}