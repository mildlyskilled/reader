package com.mildlyskilled.listener

import com.mildlyskilled.model.DbConfiguration
import com.mildlyskilled.repository.db.JdbcRepository
import com.mildlyskilled.repository.db.createDb
import com.mildlyskilled.repository.db.destroyDb
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.extensions.TestListener
import org.junit.platform.commons.logging.LoggerFactory

object ServiceTestListener : TestListener {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val config: Config = ConfigFactory.load()
    private var started = 0L
    private val dbConfig = config.extract<DbConfiguration>("db")
    val repo = JdbcRepository(dbConfig)

    override fun beforeSpec(description: Description, spec: Spec) {
        logger.info { "Attempting migration on ${dbConfig.url}" }
        createDb()
    }

    override fun afterSpec(description: Description, spec: Spec) {
        val time = System.currentTimeMillis() - started
        logger.warn { "overall time [ms]: $time" }
        attemptClean(0, "No failure message")
    }

    private fun attemptClean(attempts: Int, failureMessage: String) {
        try {
            if (attempts < 5) {
                destroyDb()
            } else throw Exception("Too many attempts to clean the database $failureMessage")
        } catch (e: Exception) {
            println("Backing off for 5 seconds")
            Thread.sleep(5000)
            attemptClean(attempts + 1, e.localizedMessage)
        }
    }
}
