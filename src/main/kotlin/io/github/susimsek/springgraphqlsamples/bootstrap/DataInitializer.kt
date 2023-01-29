package io.github.susimsek.springgraphqlsamples.bootstrap

import io.github.susimsek.springgraphqlsamples.domain.Role
import io.github.susimsek.springgraphqlsamples.graphql.enumerated.RoleName
import io.github.susimsek.springgraphqlsamples.repository.RoleRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component


@Component
@ConditionalOnProperty(
    value = ["command.line.runner.enabled"],
    havingValue = "true",
    matchIfMissing = true
)
class DataInitializer(
    private val roleRepository: RoleRepository
): CommandLineRunner {

    companion object {
        private val log = LoggerFactory.getLogger(DataInitializer::class.java)
    }


    override fun run(vararg args: String?) {

        val data = listOf(
            Role(name = RoleName.ROLE_USER),
            Role(name = RoleName.ROLE_ADMIN)
        )

        runBlocking {
            if (roleRepository.count() == 0L) {
                roleRepository.saveAll(data)
                    .map {
                        log.debug("saved: $it")
                    }.collect()
            }
        }
    }

}