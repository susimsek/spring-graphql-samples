package io.github.susimsek.springgraphqlsamples.domain

import io.github.susimsek.springgraphqlsamples.domain.audit.AbstractUserAuditingEntity
import org.springframework.data.annotation.Id
import java.util.*

open class BaseEntity(
    @Id
    var id: String = UUID.randomUUID().toString()
) : AbstractUserAuditingEntity()
