package com.mildlyskilled.repository.db

import com.mildlyskilled.model.DbConfiguration
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

open class JdbcRepository(config: DbConfiguration) {
    private fun dataSource(config: DbConfiguration) = HikariDataSource(HikariConfig().apply {
        driverClassName = config.driver
        username = config.user
        password = config.password
        jdbcUrl = config.url
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    })

    init {
        Database.connect(dataSource(config))
    }

    fun offset(page: Int, limit: Int): Long = ((page - 1) * limit).toLong()
}
