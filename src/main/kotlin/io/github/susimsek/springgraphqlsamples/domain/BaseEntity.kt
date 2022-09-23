package io.github.susimsek.springgraphqlsamples.domain

import io.github.susimsek.springgraphqlsamples.domain.audit.AbstractUserAuditingEntity
import org.springframework.data.annotation.Id

open class BaseEntity(
    @Id
    var id: String? = null
) : AbstractUserAuditingEntity()
