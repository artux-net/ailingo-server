package net.artux.ailingo.server

import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait

@Suppress("UtilityClassWithPublicConstructor")
@ActiveProfiles(profiles = ["default", "dev", "test"])
open class AbstractTest {

    companion object {

        private val db = PostgreSQLContainer("postgres:16")
            .withDatabaseName("pdanet")
            .withUsername("pdanet")
            .withPassword("pdanet")
            .withExposedPorts(5432)
            .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*", 1))
            .withReuse(true)

        init {
            db.start()
        }

        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", db::getJdbcUrl)
            registry.add("spring.datasource.username", db::getUsername)
            registry.add("spring.datasource.password", db::getPassword)
            registry.add("jwt.secret") { "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970" }
        }
    }
}
