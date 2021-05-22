package com.mildlyskilled.model.outgoing

import java.util.UUID

data class Reader(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val email: String,
    val created: String,
    val avatar: String? = null
)

data class Token(
    val token: String,
    val refresh: String?
)
