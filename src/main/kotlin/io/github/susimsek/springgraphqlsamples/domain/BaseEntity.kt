package io.github.susimsek.springgraphqlsamples.domain

import io.github.susimsek.springgraphqlsamples.domain.audit.AbstractUserAuditingEntity
import org.springframework.data.annotation.Id
import java.io.Serializable
import java.util.*

open class BaseEntity(
    @Id
    open var id: String = UUID.randomUUID().toString()
) : AbstractUserAuditingEntity(), Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }
}
