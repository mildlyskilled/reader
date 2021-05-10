package com.mildlyskilled.model.incoming

data class NewReaderRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)