package com.mildlyskilled.model.common

import arrow.core.Either

sealed class Result {
    companion object Factory {
        inline fun <V> build(function: () -> V): Either<Exception, V> =
            try {
                Either.Right(function.invoke())
            } catch (e: Exception) {
                Either.Left(e)
            } catch (re: RuntimeException) {
                Either.Left(re)
            }
    }
}