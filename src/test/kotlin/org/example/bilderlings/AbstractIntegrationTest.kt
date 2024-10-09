package org.example.bilderlings

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers


@Testcontainers
@ActiveProfiles("test")
@ContextConfiguration(initializers = [AbstractIntegrationTest.Companion.Initializer::class])
open class AbstractIntegrationTest {

    companion object {
        @Container
        private val postgresContainer = PostgreSQLContainer<Nothing>("postgres:14.3").apply {
            withDatabaseName("testdb")
            withUsername("test")
            withPassword("test")
        }

        @Container
        private val redisContainer = GenericContainer<Nothing>("redis:6.2").apply {
            withExposedPorts(6379)
        }

        @JvmStatic
        @BeforeAll
        fun setUp() {
            postgresContainer.start()
            redisContainer.start()
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            postgresContainer.stop()
            redisContainer.stop()
        }

        class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
            override fun initialize(context: ConfigurableApplicationContext) {
                val values = TestPropertyValues.of(
                    "spring.datasource.url=${postgresContainer.jdbcUrl}",
                    "spring.datasource.username=${postgresContainer.username}",
                    "spring.datasource.password=${postgresContainer.password}",
                    "spring.liquibase.url=${postgresContainer.jdbcUrl}",
                    "spring.liquibase.user=${postgresContainer.username}",
                    "spring.liquibase.password=${postgresContainer.password}",
                    "spring.data.redis.host=${redisContainer.host}",
                    "spring.data.redis.port=${redisContainer.getMappedPort(6379)}"
                )
                values.applyTo(context.environment)
            }
        }
    }
}
